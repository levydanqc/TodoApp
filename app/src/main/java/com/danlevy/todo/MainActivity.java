package com.danlevy.todo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.danlevy.todo.data.AppExecutors;
import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.databinding.ActivityMainBinding;
import com.danlevy.todo.model.Tache;
import com.danlevy.todo.ui.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private TodoRoomDatabase mDb;
    private static final int CAMERA_PERMISSION_CODE = 1;
    HomeViewModel homeViewModel;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            isAdmin = savedInstanceState.getBoolean("isAdmin");

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDb = TodoRoomDatabase.getDatabase(this);
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDb.todoDao().insert(new Tache("Ménage", "Nettoyer la cuisine"));
            mDb.todoDao().insert(new Tache("Miscellaneous", "Installer les caméras de sécurité"));
            mDb.todoDao().insert(new Tache("Jardin", "Ramasser les tomates"));
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_afaire)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
                    dialog.setPositiveButton("Ok", (dialog1, which) -> requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE));
                    dialog.setNegativeButton("Annuler", (dialog12, which) -> Toast.makeText(this, "Impossible d'activer le mode Admin.", Toast.LENGTH_SHORT).show());
                    dialog.show();
                } else {
                    Toast.makeText(this, "Permission refusé...", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    public void admin(boolean admin) {
        isAdmin = admin;
        homeViewModel.setIsAdmin(admin);
        findViewById(R.id.fab).setVisibility(admin ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_admin:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else admin(!isAdmin && ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
                return true;
            case R.id.bt_delete_all:
                AppExecutors.getInstance().diskIO().execute(() -> mDb.todoDao().deleteAll());
                Toast.makeText(this, "Suppression de toutes les tâches...", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isAdmin", isAdmin);
    }
}