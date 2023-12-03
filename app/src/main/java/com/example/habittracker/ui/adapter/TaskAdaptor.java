package com.example.habittracker.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habittracker.addnew.AddNewTask;
import com.example.habittracker.MainActivity;
import com.example.habittracker.R;
import com.example.habittracker.database.TaskDatabaseHelper;
import com.example.habittracker.ui.task.TaskModel;

import java.util.List;

public class TaskAdaptor extends RecyclerView.Adapter<TaskAdaptor.MyViewHolder> {

    private List<TaskModel> taskList;
    private MainActivity activity;
    private TaskDatabaseHelper db;

    public TaskAdaptor(MainActivity activity, TaskDatabaseHelper db) {
        this.activity = activity;
        this.db = db;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout , parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final TaskModel item = taskList.get(position);
        holder.taskBox.setText(item.getTask());
        holder.taskBox.setChecked(toBoolean(item.getStatus()));
        holder.taskBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Remove the item from the database
                    db.deleteTask(item.getId());
                    // Remove the item from the RecyclerView
                    taskList.remove(item);
                    notifyItemRemoved(holder.getAdapterPosition());

                    if (activity != null) {
                        activity.updateExpBar(10);
                    } else {
                        Log.d("ToDoAdapter", "MainActivity is null");
                    }

                    notifyDataSetChanged();
                }
            }
        });
    }




    public boolean toBoolean(int num) {
        return num != 0;
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<TaskModel> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        TaskModel item = taskList.get(position);
        db.deleteTask(item.getId());
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        TaskModel item = taskList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());

        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), task.getTag());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskBox = itemView.findViewById(R.id.mcheckbox);
        }
    }
}
