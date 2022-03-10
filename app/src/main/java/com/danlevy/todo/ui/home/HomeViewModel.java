package com.danlevy.todo.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.model.Tache;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final LiveData<List<Tache>> alltodos;
    private final MutableLiveData<Boolean> isAdmin;

    public HomeViewModel(Application application) {
        super(application);
        TodoRoomDatabase mDb = TodoRoomDatabase.getDatabase(application);
        alltodos = mDb.todoDao().getTodos();
        isAdmin = new MutableLiveData<>();
        this.isAdmin.setValue(false);
    }

    public LiveData<List<Tache>> getAllTodo() {
        return alltodos;
    }

    public MutableLiveData<Boolean> getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin.setValue(admin);
    }
}