package com.danlevy.todo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.danlevy.todo.model.Tache;

import java.util.List;


@Dao
public interface TodoDao {
    @Insert
    void insert(Tache tache);

    @Query("select * from todo where id = :id")
    Tache getTodo(int id);

    @Query("delete from todo")
    void deleteAll();

    @Query("delete from todo where id = :id")
    void delete(int id);

    @Query("update todo set title = :title, info = :info, chosen = :chosen where id = :id")
    void updateTache(int id, String title, String info, Boolean chosen);

    @Query("select * from todo order by title DESC")
    LiveData<List<Tache>> getTodos();

    @Query("select * from todo where chosen = 1")
    LiveData<List<Tache>> getAFaire();

}
