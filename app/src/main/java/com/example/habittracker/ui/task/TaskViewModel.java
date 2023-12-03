package com.example.habittracker.ui.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.habittracker.database.TaskDatabaseHelper;

import java.util.Collections;
import java.util.List;

public class TaskViewModel extends ViewModel {
        private static MutableLiveData<List<TaskModel>> taskListLiveData = new MutableLiveData<>();

        public LiveData<List<TaskModel>> getTaskListLiveData() {
            return taskListLiveData;
        }

        public static void refreshData(TaskDatabaseHelper db) {
            List<TaskModel> taskList = db.getAllTasks();
            Collections.reverse(taskList);
            taskListLiveData.setValue(taskList);
        }

}