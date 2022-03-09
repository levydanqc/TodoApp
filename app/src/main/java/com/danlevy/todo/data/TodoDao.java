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

    @Query("delete from todo")
    void deleteAll();

    @Query("delete from todo where id = :id")
    int delete(int id);

    @Query("update todo set title = :title, info = :info, chosen = :chosen where id = :id")
    int updateTache(int id, String title, String info, Boolean chosen);

    @Query("select * from todo where chosen = 0 order by title DESC")
    LiveData<List<Tache>> getTodos();

    @Query("select * from todo where chosen = 1")
    LiveData<List<Tache>> getAFaire();

}
