package com.example.habittracker.ui.habit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.database.HabitDatabaseHelper;

import java.util.Collections;
import java.util.List;

public class HabitViewModel extends ViewModel {
    private static MutableLiveData<List<HabitModel>> taskListLiveData = new MutableLiveData<>();

    public LiveData<List<HabitModel>> getTaskListLiveData() {
        return taskListLiveData;
    }

    public static void refreshData(HabitDatabaseHelper db) {
        List<HabitModel> taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskListLiveData.setValue(taskList);
    }

}