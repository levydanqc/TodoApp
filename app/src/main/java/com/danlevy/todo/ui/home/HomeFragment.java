package com.danlevy.todo.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danlevy.todo.R;
import com.danlevy.todo.data.AppExecutors;
import com.danlevy.todo.databinding.FragmentHomeBinding;
import com.danlevy.todo.model.Tache;
import com.danlevy.todo.ui.TodoListAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private TodoListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.rv_todos);
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        adapter = new TodoListAdapter(context);
        recyclerView.setAdapter(adapter);

        registerForContextMenu(recyclerView);

        homeViewModel.getAllTodo().observe(getViewLifecycleOwner(), new Observer<List<Tache>>() {
            @Override
            public void onChanged(List<Tache> taches) {
                adapter.setTodos(taches);
                adapter.notifyDataSetChanged();
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}