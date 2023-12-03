package com.example.habittracker.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habittracker.ProgressUtil;
import com.example.habittracker.addnew.AddNewHabit;
import com.example.habittracker.database.HabitDatabaseHelper;
import com.example.habittracker.MainActivity;
import com.example.habittracker.R;
import com.example.habittracker.ui.habit.HabitModel;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.MyViewHolder> {

    private List<HabitModel> habitList;
    private MainActivity activity;
    private HabitDatabaseHelper db;

    public HabitAdapter(MainActivity activity, HabitDatabaseHelper db) {
        this.activity = activity;
        this.db = db;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_layout , parent , false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final HabitModel item = habitList.get(position);
        holder.textView.setText(item.getTask());
        holder.positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Positive clicked
                if (activity != null) {
                    activity.updateExpBar(10);
                } else {
                    Log.d("ToDoAdapter", "MainActivity is null");
                }

                notifyDataSetChanged();
            }
        });
        holder.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Negative clicked
                if (activity != null) {
                    activity.updateExpBar(-10);
                } else {
                    Log.d("ToDoAdapter", "MainActivity is null");
                }

                notifyDataSetChanged();
            }
        });
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<HabitModel> habitList) {
        this.habitList = habitList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        HabitModel item = habitList.get(position);
        db.deleteTask(item.getId());
        habitList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        HabitModel item = habitList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());

        AddNewHabit habit = new AddNewHabit();
        habit.setArguments(bundle);
        habit.show(activity.getSupportFragmentManager(), habit.getTag());

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton positiveBtn;
        ImageButton negativeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.taskView);
            positiveBtn = itemView.findViewById(R.id.positive);
            negativeBtn = itemView.findViewById(R.id.negative);
        }
    }
}

