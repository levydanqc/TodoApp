package com.danlevy.todo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.danlevy.todo.model.Tache;


@Dao
public interface TodoDao {
    @Insert
    void insert(Tache tache);

    @Query("delete from todo")
    void deleteAll();

    @Delete
    int delete(Tache tache);

    @Query("update todo set title = :title and info = :info where id = :id")
    int updateTache(int id, String title, String info);

    @Query("select * from todo order by title DESC")
    LiveData<List<Tache>> getAll();

    @Query("select count(*) from todo")
    int count();
}
