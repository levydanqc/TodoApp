package ca.cegepgarneau.todo.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ca.cegepgarneau.todo.data.TodoRoomDatabase;
import ca.cegepgarneau.todo.model.Todo;

public class HomeViewModel extends AndroidViewModel {

    private LiveData<List<Todo>> alltodos;
    private TodoRoomDatabase mDb;

    public HomeViewModel(Application application) {
        super(application);
        mDb = TodoRoomDatabase.getDatabase(application);
        alltodos = mDb.todoDao().getAllTodo();
    }

    public LiveData<List<Todo>> getAllTodo() {
        return alltodos;
    }
}