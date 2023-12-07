package com.example.habittracker.ui.daily;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class DailyFragment extends Fragment implements onDialogClose, SensorEventListener {

    private FragmentDailyBinding binding;
    private RecyclerView recyclerView;
    private static final String MIDNIGHT_RESET_ACTION = "com.example.habittracker.MIDNIGHT_RESET_ACTION";
    private PendingIntent pendingIntent;
    private HealthBarUpdateListener healthBarUpdateListener;
    private HealthDatabaseHelper db;
    private List<DailyModel> dailyList;
    private HealthAdapter adapter;
    private DailyViewModel dailyViewModel;
    private SensorManager mSensorManager = null;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private int previewsTotalSteps = 0;
    private ProgressBar progressBar;
    private ProgressBar expBar;

    private TextView steps;

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

        progressBar = root.findViewById(R.id.progressBar5);
        expBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        steps = root.findViewById(R.id.textView);

        resetSteps();
        loadData();

        mSensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


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

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MIDNIGHT_RESET_ACTION);
        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Schedule alarm every 24 hours
        long midnight = getMidnightTime();
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, midnight, AlarmManager.INTERVAL_DAY, pendingIntent);

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

    private long getMidnightTime() {
        long now = SystemClock.elapsedRealtime();
        long midnight = now - (now % (24 * 60 * 60 * 1000)) + (24 * 60 * 60 * 1000);
        return midnight;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (stepSensor == null){
            Toast.makeText(requireContext(), "This device has no sensor", Toast.LENGTH_SHORT).show();
        } else {
            mSensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        requireContext().registerReceiver(midnightResetReceiver, new IntentFilter(MIDNIGHT_RESET_ACTION));
    }

    @Override
    public void onPause(){
        super.onPause();
        requireContext().unregisterReceiver(midnightResetReceiver);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            totalSteps = (int)sensorEvent.values[0];
            int currentSteps = totalSteps-previewsTotalSteps;
            steps.setText("Steps: " + currentSteps);
            Log.d("Steps", "Steps: " + currentSteps);
            progressBar.setProgress(currentSteps);
            int maxProgressBarValue = progressBar.getMax();

            if (currentSteps >= maxProgressBarValue) {
                updateHealthBar(10);
            }
        }

    }

    @Override
    public void onDestroyView() {
        // Cancel the pending intent
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        super.onDestroyView();
        binding = null;
    }

    private BroadcastReceiver midnightResetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Reset steps at midnight
            previewsTotalSteps = totalSteps;
            steps.setText("0");
            progressBar.setProgress(0);
            saveData();
        }
    };
    private void resetSteps(){
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Long press to reset steps", Toast.LENGTH_SHORT);
            }
        });

        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                previewsTotalSteps = totalSteps;
                steps.setText("0");
                progressBar.setProgress(0);
                saveData();
                return true;
            }
        });
    }

    private void saveData(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key1",String.valueOf(previewsTotalSteps));
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int savedNumber = (int) sharedPref.getFloat("key1", 0f);
        previewsTotalSteps = savedNumber;
    }
    public void setHealthBarUpdateListener(HealthBarUpdateListener listener) {
        this.healthBarUpdateListener = listener;
    }

    // Call this method when you want to update the expBar
    private void updateHealthBar(int increment) {
        if (healthBarUpdateListener != null) {
            healthBarUpdateListener.healthUpdate(increment, null);
        }
    }


    public interface HealthBarUpdateListener {
        void healthUpdate(int increment, TextView txt);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
