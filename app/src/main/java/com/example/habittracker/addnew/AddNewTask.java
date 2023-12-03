package com.example.habittracker.addnew;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.habittracker.database.TaskDatabaseHelper;
import com.example.habittracker.R;
import com.example.habittracker.onDialogClose;
import com.example.habittracker.ui.task.TaskModel;
import com.example.habittracker.ui.task.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";
    private EditText EditTaskText;
    private Button SaveTextButton;
    private TaskDatabaseHelper db;
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.add_newtask, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditTaskText = view.findViewById(R.id.addTaskText);
        SaveTextButton = view.findViewById(R.id.saveButton);

        db = new TaskDatabaseHelper(getActivity());

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            EditTaskText.setText(task);

            if (task.length() > 0 ){
                SaveTextButton.setEnabled(false);
            }
        }

        EditTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Basically a color changer for save button
                if (s.toString().equals("")){
                    SaveTextButton.setEnabled(false);
                    SaveTextButton.setBackgroundColor(Color.GRAY);
                }else{
                    SaveTextButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final boolean finalIsUpdate = isUpdate;
        SaveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = EditTaskText.getText().toString();

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                    TaskViewModel.refreshData(db);
                    
                } else {
                    TaskModel item = new TaskModel();
                    item.setTask(text);
                    item.setStatus(0);
                    db.insertTask(item);

                    // Notify LiveData that data has changed
                    TaskViewModel.refreshData(db);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof onDialogClose){
            ((onDialogClose)activity).onDialogClose(dialog);
        }
    }
}
