package com.example.habittracker.ui.daily;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habittracker.database.HealthDatabaseHelper;
import com.example.habittracker.MainActivity;
import com.example.habittracker.R;
import com.example.habittracker.databinding.FragmentDailyBinding;
import com.example.habittracker.onDialogClose;
import com.example.habittracker.ui.adapter.HealthAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DailyFragment extends Fragment implements onDialogClose {

    private FragmentDailyBinding binding;
    private RecyclerView recyclerView;
    private HealthDatabaseHelper db;
    private List<DailyModel> dailyList;
    private HealthAdapter adapter;
    private DailyViewModel dailyViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dailyViewModel = new ViewModelProvider(this).get(DailyViewModel.class);
        dailyViewModel.getTaskListLiveData().observe(getViewLifecycleOwner(), new Observer<List<DailyModel>>() {
            @Override
            // updating task changed
            public void onChanged(List<DailyModel> dailyList) {
                adapter.setTasks(dailyList);
                adapter.notifyDataSetChanged();
            }
        });

        binding = FragmentDailyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Database updater
        recyclerView = root.findViewById(R.id.recyclerview2);
        db = new HealthDatabaseHelper(requireContext());
        dailyList = new ArrayList<>();
        adapter = new HealthAdapter((MainActivity) requireActivity(), db);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        dailyList = db.getAllTasks();
        Collections.reverse(dailyList);
        adapter.setTasks(dailyList);


        return root;
    }
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        // Notify update
        dailyList = db.getAllTasks();
        Collections.reverse(dailyList);
        adapter.setTasks(dailyList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
