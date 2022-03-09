package com.danlevy.todo.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.model.Tache;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final LiveData<List<Tache>> alltodos;

    public HomeViewModel(Application application) {
        super(application);
        TodoRoomDatabase mDb = TodoRoomDatabase.getDatabase(application);
        alltodos = mDb.todoDao().getTodos();
    }

    public LiveData<List<Tache>> getAllTodo() {
        return alltodos;
    }
}