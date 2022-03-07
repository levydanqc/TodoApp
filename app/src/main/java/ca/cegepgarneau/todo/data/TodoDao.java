package ca.cegepgarneau.todo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ca.cegepgarneau.todo.model.Todo;


@Dao
public interface TodoDao {
    @Insert
    void insert(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("DELETE FROM todo_table")
    void deleteAll();

    @Query("SELECT * FROM todo_table ORDER BY id DESC")
    LiveData<List<Todo>> getAllTodo();

}
