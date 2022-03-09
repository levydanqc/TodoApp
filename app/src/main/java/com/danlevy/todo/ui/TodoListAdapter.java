package com.danlevy.todo.ui;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.danlevy.todo.R;
import com.danlevy.todo.data.AppExecutors;
import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.model.Tache;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private List<Tache> tacheList;
    private TodoRoomDatabase mDb;
    private Context context;

    public TodoListAdapter(Context context) {
        this.context = context;
    }


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
        if (tacheList != null) {
            Tache current = tacheList.get(position);
            todoViewHolder.title.setText(current.getTitle());
            todoViewHolder.info.setText(current.getInfo());

            int resID = context.getResources().getIdentifier("avatar" + current.getIcon(), "drawable", context.getPackageName());
            todoViewHolder.icon.setImageDrawable(context.getResources().getDrawable(resID));

            todoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAG", "Chosen: " + current.getChosen());
                            mDb.todoDao().updateTache(current.getId(), current.getTitle(), current.getInfo(), !current.getChosen());
                        }
                    });
                }
            });
        }
    }

    public void setTodos(List<Tache> taches) {
        this.tacheList = taches;
    }

    public List<Tache> getTodos() {
        return tacheList;
    }


    @Override
    public int getItemCount() {
        if (tacheList != null)
            return tacheList.size();
        else return 0;
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView title;
        public TextView info;
        public ImageView icon;
        public CardView card;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_todo);
            info = itemView.findViewById(R.id.tv_info);
            icon = itemView.findViewById(R.id.iv_icon);
            card = itemView.findViewById(R.id.card);
            card.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 0, 0, "Modifier");
            menu.add(this.getAdapterPosition(), 1, 1, "Supprimer");
        }
    }


}
