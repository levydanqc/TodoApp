package ca.cegepgarneau.todo.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.cegepgarneau.todo.model.Todo;


@Database(entities = {Todo.class}, version = 1, exportSchema = true)
public abstract class TodoRoomDatabase extends RoomDatabase {
    // abstract, ce n'est pas une classe d'implémentation
    // l'implémentation est fournie par le room

    // Singleton
    public static volatile TodoRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // DAO
    public abstract TodoDao todoDao();

    public static TodoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Crée la BDD
            synchronized (TodoRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TodoRoomDatabase.class, "todo_database")
                        .addCallback(sRoomDatabaseCallback)
                        .build();
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    Log.d("TAG", "onCreate: ");

                    // If you want to keep data through app restarts,
                    // comment out the following block
                    databaseWriteExecutor.execute(() -> {
                        // Populate the database in the background.
                        // If you want to start with more words, just add them.
                        TodoDao dao = INSTANCE.todoDao();
//                        dao.deleteAll();

                        Todo todo = new Todo("Hello");
                        dao.insert(todo);
                        todo = new Todo("World");
                        dao.insert(todo);
                    });

                }
            };

}
