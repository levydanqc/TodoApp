package ca.cegepgarneau.todo.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.todo.R;
import ca.cegepgarneau.todo.data.AppExecutors;
import ca.cegepgarneau.todo.data.TodoRoomDatabase;
import ca.cegepgarneau.todo.model.Todo;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private List<Todo> todoList;
    private TodoRoomDatabase mDb;

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup, false);
        mDb = TodoRoomDatabase.getDatabase(viewGroup.getContext());
        return new TodoListAdapter.TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder todoViewHolder, int position) {
//        if (todoList != null) {
        Todo current = todoList.get(position);
        todoViewHolder.todoTextView.setText(current.getTodo());
//        } else {
//            todoViewHolder.todoTextView.setText(R.string.no_notodo);
//        }

        todoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "onBindViewHolder: " + current.getId());
                        mDb.todoDao().delete(current);
                    }
                });
            }
        });
    }

    public void setTodos(List<Todo> todos) {
        this.todoList = todos;
    }

    public List<Todo> getTodos() {
        return todoList;
    }


    @Override
    public int getItemCount() {
        if (todoList != null)
            return todoList.size();
        else return 0;
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        public TextView todoTextView;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.tv_todo);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (listener != null && position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(todoList.get(position));
//                    }
//                }
//            });
        }
    }


}
