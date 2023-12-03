package com.example.habittracker.ui.daily;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.database.HealthDatabaseHelper;

import java.util.Collections;
import java.util.List;

public class DailyViewModel extends ViewModel {
    private static MutableLiveData<List<DailyModel>> taskListLiveData = new MutableLiveData<>();
    // Live data handler
    public LiveData<List<DailyModel>> getTaskListLiveData() {
        return taskListLiveData;
    }

    public static void refreshData(HealthDatabaseHelper db) {
        List<DailyModel> dailyList = db.getAllTasks();
        Collections.reverse(dailyList);
        taskListLiveData.setValue(dailyList);
    }

}