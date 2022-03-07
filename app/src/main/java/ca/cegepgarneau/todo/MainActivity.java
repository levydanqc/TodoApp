package ca.cegepgarneau.todo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ca.cegepgarneau.todo.data.AppExecutors;
import ca.cegepgarneau.todo.data.TodoRoomDatabase;
import com.example.todo.databinding.ActivityMainBinding;
import ca.cegepgarneau.todo.model.Todo;
import ca.cegepgarneau.todo.ui.home.HomeViewModel;

public class MainActivity extends AppCompatActivity {

    private HomeViewModel homeViewModel;
    private ActivityMainBinding binding;
    private TodoRoomDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDb = TodoRoomDatabase.getDatabase(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Todo todo = new Todo("Acheter une maison");
                mDb.todoDao().insert(todo);
//                mDb.todoDao().deleteAll();
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_add:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Todo todo = new Todo("Acheter une maison");
                        mDb.todoDao().insert(todo);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}