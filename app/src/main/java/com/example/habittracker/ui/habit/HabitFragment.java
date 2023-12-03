package com.example.habittracker.ui.habit;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.example.habittracker.database.HabitDatabaseHelper;
import com.example.habittracker.MainActivity;
import com.example.habittracker.R;
import com.example.habittracker.recycle.HabitTouchHelper;
import com.example.habittracker.databinding.FragmentHabitBinding;
import com.example.habittracker.onDialogClose;
import com.example.habittracker.ui.adapter.HabitAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HabitFragment extends Fragment implements onDialogClose {

    private FragmentHabitBinding binding;
    private RecyclerView recyclerView;
    private HabitDatabaseHelper db;
    private List<HabitModel> habitList;
    private HabitAdapter adapter;
    private HabitViewModel habitViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        habitViewModel.getTaskListLiveData().observe(getViewLifecycleOwner(), new Observer<List<HabitModel>>() {
            @Override
            public void onChanged(List<HabitModel> habitList) {
                adapter.setTasks(habitList);
                adapter.notifyDataSetChanged();
            }
        });

        binding = FragmentHabitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Database updater
        recyclerView = root.findViewById(R.id.recyclerview3);
        db = new HabitDatabaseHelper(requireContext());
        habitList = new ArrayList<>();
        adapter = new HabitAdapter((MainActivity) requireActivity(), db);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set up ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new HabitTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        habitList = db.getAllTasks();
        Collections.reverse(habitList);
        adapter.setTasks(habitList);


        return root;
    }
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        // Notify update
        habitList = db.getAllTasks();
        Collections.reverse(habitList);
        adapter.setTasks(habitList);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
