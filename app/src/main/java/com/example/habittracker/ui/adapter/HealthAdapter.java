package com.example.habittracker.ui.adapter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habittracker.database.HealthDatabaseHelper;
import com.example.habittracker.MainActivity;
import com.example.habittracker.ui.daily.DailyModel;
import com.example.habittracker.R;

import java.util.Calendar;
import java.util.List;

public class HealthAdapter extends RecyclerView.Adapter<HealthAdapter.MyViewHolder> {

    private List<DailyModel> dailyList;
    private MainActivity activity;
    private HealthDatabaseHelper db;

    private Handler handler = new Handler(Looper.getMainLooper());
    public HealthAdapter(MainActivity activity, HealthDatabaseHelper db) {
        this.activity = activity;
        this.db = db;
        scheduleMidnightTask();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout , parent , false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final DailyModel item = dailyList.get(position);

        holder.taskBox.setText(item.getTask());
        holder.taskBox.setChecked(toBoolean(item.getStatus()));
        holder.taskBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    db.updateStatus(item.getId(), 1);
                    if (activity != null) {
                        // Health and EXP updated
                        activity.updateExpBar(10);
                        activity.healthUpdate(10, activity.findViewById(R.id.healthProgres));
                    } else {
                        Log.d("ToDoAdapter", "MainActivity is null");
                    }
                    holder.taskBox.setEnabled(false);
                } else {
                    db.updateStatus(item.getId(), 0);

                }

            }
        });
    }

    public boolean toBoolean(int num) {
        return num != 0;
    }

    public void setTasks(List<DailyModel> dailyList) {
        this.dailyList = dailyList;
        notifyDataSetChanged();
    }

    private void scheduleMidnightTask() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if it's midnight and reset checkboxes
                resetCheckBoxesAtMidnight();

                // Schedule the next task for the next day
                scheduleMidnightTask();
            }
        }, getNextMidnightDelay());
    }

    // Reset Health dailies in midnight
    private long getNextMidnightDelay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long currentTimeMillis = System.currentTimeMillis();
        long midnightMillis = calendar.getTimeInMillis();

        // Calculate the delay until the next midnight
        long delayMillis = midnightMillis - currentTimeMillis;

        return delayMillis;
    }

    private void resetCheckBoxesAtMidnight() {
        // Check if midnight and reset checkboxes
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (currentHour == 0) {
            resetAllCheckBoxes();
        }
    }

    private void resetAllCheckBoxes() {
        int healthDecrement = 10;

        // Reset all checkboxes and decrement health for unchecked ones
        for (DailyModel item : dailyList) {
            if (item.getStatus() == 1) {
                db.updateStatus(item.getId(), 0);
            } else {
                db.updateStatus(item.getId(), 0);
                // Subtract 10 from the health bar for each unchecked checkbox
                if (activity != null) {
                    activity.healthUpdate(-healthDecrement, activity.findViewById(R.id.healthProgres));
                }
            }
        }

        // Refresh data
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }




    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}

