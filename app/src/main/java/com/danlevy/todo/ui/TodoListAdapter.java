package com.danlevy.todo.ui;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danlevy.todo.R;
import com.danlevy.todo.data.AppExecutors;
import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.model.Tache;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private List<Tache> tacheList;
    private TodoRoomDatabase mDb;
    private final Context context;
    private boolean isAdmin;

    public TodoListAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup, false);
        mDb = TodoRoomDatabase.getDatabase(viewGroup.getContext());
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder todoViewHolder, int position) {
        if (tacheList != null) {
            Tache current = tacheList.get(position);
            todoViewHolder.title.setText(current.getTitle());
            todoViewHolder.info.setText(current.getInfo());
            todoViewHolder.id = current.getId();
            todoViewHolder.isAdmin = isAdmin;

            int resID = context.getResources().getIdentifier("avatar" + current.getIcon(), "drawable", context.getPackageName());
            todoViewHolder.icon.setImageDrawable(context.getResources().getDrawable(resID));

            todoViewHolder.itemView.setOnClickListener(v ->
                    AppExecutors.getInstance().diskIO().execute(() ->
                            mDb.todoDao().updateTache(current.getId(), current.getTitle(), current.getInfo(), true)));
        }
    }

    public void setTodos(List<Tache> taches) {
        this.tacheList = taches;
    }

    @Override
    public int getItemCount() {
        if (tacheList != null)
            return tacheList.size();
        else return 0;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView title;
        public TextView info;
        public ImageView icon;
        public int id;
        private boolean isAdmin;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_todo);
            info = itemView.findViewById(R.id.tv_info);
            icon = itemView.findViewById(R.id.iv_icon);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(@NonNull ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (isAdmin) {
                menu.add(id, 0, 0, "Modifier");
                menu.add(id, 1, 1, "Supprimer");
            }
        }
    }


}
