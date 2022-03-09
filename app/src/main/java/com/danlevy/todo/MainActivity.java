package com.danlevy.todo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.danlevy.todo.data.AppExecutors;
import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.databinding.ActivityMainBinding;
import com.danlevy.todo.model.Tache;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {
    private static final int CAMERA_PERMISSION_CODE = 1;
    private ActivityMainBinding binding;
    private TodoRoomDatabase mDb;
    private FloatingActionButton fab;
    private boolean admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mDb = TodoRoomDatabase.getDatabase(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.todoDao().insert(new Tache("Ménage", "Nettoyer la cuisine"));
                mDb.todoDao().insert(new Tache("Miscellaneous", "Installer les caméras de sécurité"));
                mDb.todoDao().insert(new Tache("Jardin", "Ramasser les tomates"));
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_afaire)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (((FragmentNavigator.Destination) navDestination).getClassName().equals("com.danlevy.todo.ui.afaire.AFaireFragment")) {
                    fab.setVisibility(View.INVISIBLE);
                } else if (admin) {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void showModal(String title, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titre = new EditText(context);
        final EditText info = new EditText(context);

        if (id == -1) {
            titre.setHint("Titre");
            info.setHint("Description");
        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Tache tache = mDb.todoDao().getTodo(id);
                    titre.setText(tache.getTitle());
                    info.setText(tache.getInfo());
                }
            });
        }

        layout.addView(titre);
        layout.addView(info);
        builder.setView(layout);
        builder.setPositiveButton("Valider", this);
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                !titre.getText().toString().isEmpty() && !info.getText().toString().isEmpty());

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                        !titre.getText().toString().isEmpty() && !info.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        titre.addTextChangedListener(watcher);
        info.addTextChangedListener(watcher);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == -1) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.todoDao().insert(new Tache(titre.getText().toString(), info.getText().toString()));
                        }
                    });
                } else {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.todoDao().updateTache(id, titre.getText().toString(), info.getText().toString(), false);
                        }
                    });
                }
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showModal("Modifier une tache", item.getGroupId());
                break;
            case 1:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.todoDao().delete(item.getGroupId());
                    }
                });
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                showModal("Ajouter une tache", -1);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_admin:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && !admin) {
                    admin(true);
                } else {
                    admin(false);
                }
                return true;
            case R.id.bt_delete_all:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.todoDao().deleteAll();
                    }
                });
                Toast.makeText(this, "Suppression de toutes les tâches...", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void admin(boolean isAdmin) {
        admin = isAdmin;
        fab.setVisibility(isAdmin ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                admin(true);
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.CAMERA)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Accès à la caméra");
                    dialog.setMessage("L'accès à la caméra est nécessaire à l'activation du mode Admin" +
                            "pour une reconnaissance faciale.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    });
                    dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Impossible d'activer le mode Admin.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(this, "Permission refusé...", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}