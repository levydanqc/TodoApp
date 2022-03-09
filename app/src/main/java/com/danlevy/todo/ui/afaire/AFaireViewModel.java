package com.danlevy.todo.ui.afaire;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.model.Tache;

import java.util.List;

public class AFaireViewModel extends AndroidViewModel {
    private final LiveData<List<Tache>> allAFaire;

    public AFaireViewModel(Application application) {
        super(application);
        TodoRoomDatabase mDb = TodoRoomDatabase.getDatabase(application);
        allAFaire = mDb.todoDao().getAFaire();
    }

    public LiveData<List<Tache>> getAllAFaire() {
        return allAFaire;
    }
}