package com.example.habittracker.ui.task;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.habittracker.database.TaskDatabaseHelper;
import com.example.habittracker.MainActivity;
import com.example.habittracker.R;
import com.example.habittracker.recycle.RecycleViewTouchHelper;
import com.example.habittracker.databinding.FragmentTaskBinding;
import com.example.habittracker.onDialogClose;
import com.example.habittracker.ui.adapter.TaskAdaptor;

import java.util.Collections;
import java.util.List;

public class TaskFragment extends Fragment implements onDialogClose {

    private FragmentTaskBinding binding;
    private RecyclerView recyclerView;
    private TaskDatabaseHelper db;
    private List<TaskModel> taskList;
    private TaskAdaptor adapter;
    private TaskViewModel taskViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getTaskListLiveData().observe(getViewLifecycleOwner(), new Observer<List<TaskModel>>() {
            @Override
            // Updater and save task
            public void onChanged(List<TaskModel> taskList) {
                adapter.setTasks(taskList);
                adapter.notifyDataSetChanged();
            }
        });

        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerview);
        db = new TaskDatabaseHelper(requireContext());
        adapter = new TaskAdaptor((MainActivity) requireActivity(), db);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set up ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecycleViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        adapter.setTasks(taskList);


        return root;
    }
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        adapter.setTasks(taskList);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
