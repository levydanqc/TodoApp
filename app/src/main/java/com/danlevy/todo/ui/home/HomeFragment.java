package com.danlevy.todo.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danlevy.todo.R;
import com.danlevy.todo.data.AppExecutors;
import com.danlevy.todo.data.TodoRoomDatabase;
import com.danlevy.todo.databinding.FragmentHomeBinding;
import com.danlevy.todo.model.Tache;
import com.danlevy.todo.ui.TodoListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private FragmentHomeBinding binding;
    private TodoListAdapter adapter;
    private TodoRoomDatabase mDb;
    private boolean isAdmin = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            isAdmin = savedInstanceState.getBoolean("isAdmin");

        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getIsAdmin().observe(getViewLifecycleOwner(), this::setAdmin);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.rv_todos);
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        adapter = new TodoListAdapter(context);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.setVisibility(isAdmin ? View.VISIBLE : View.INVISIBLE);

        mDb = TodoRoomDatabase.getDatabase(getContext());

        registerForContextMenu(recyclerView);

        setHasOptionsMenu(true);

        homeViewModel.getAllTodo().observe(getViewLifecycleOwner(), taches -> {
            adapter.setTodos(taches);
            adapter.notifyDataSetChanged();
        });

        return root;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showModal(String title, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        Context context = getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titre = new EditText(context);
        final EditText info = new EditText(context);

        titre.setSingleLine();
        info.setSingleLine();

        if (id == -1) {
            titre.setHint("Titre");
            info.setHint("Description");
        } else {
            AppExecutors.getInstance().diskIO().execute(() -> {
                Tache tache = mDb.todoDao().getTodo(id);
                titre.setText(tache.getTitle());
                info.setText(tache.getInfo());
            });
        }

        layout.addView(titre);
        layout.addView(info);
        builder.setView(layout);
        builder.setPositiveButton("Valider", (dialog, which) -> {
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                !titre.getText().toString().isEmpty() && !info.getText().toString().isEmpty());

        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                        !titre.getText().toString().isEmpty() && !info.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        titre.addTextChangedListener(watcher);
        info.addTextChangedListener(watcher);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (id == -1) {
                AppExecutors.getInstance().diskIO().execute(() -> mDb.todoDao().insert(new Tache(titre.getText().toString(), info.getText().toString())));
            } else {
                AppExecutors.getInstance().diskIO().execute(() -> mDb.todoDao().updateTache(id, titre.getText().toString(), info.getText().toString(), false));
            }
            dialog.dismiss();
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showModal("Modifier une tache", item.getGroupId());
                break;
            case 1:
                AppExecutors.getInstance().diskIO().execute(() -> mDb.todoDao().delete(item.getGroupId()));
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            showModal("Ajouter une tache", -1);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        adapter.setAdmin(admin);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isAdmin", isAdmin);
    }

}