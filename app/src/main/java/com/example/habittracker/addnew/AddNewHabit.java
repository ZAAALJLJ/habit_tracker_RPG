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

import com.example.habittracker.database.HabitDatabaseHelper;
import com.example.habittracker.R;
import com.example.habittracker.onDialogClose;
import com.example.habittracker.ui.habit.HabitModel;
import com.example.habittracker.ui.habit.HabitViewModel;
import com.example.habittracker.ui.task.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewHabit extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewHabit";
    private EditText EditHabitText;
    private Button SaveHabitButton;

    private HabitDatabaseHelper db;

    public static AddNewHabit newInstance(){
        return new AddNewHabit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.add_newhabit, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditHabitText = view.findViewById(R.id.addHabitText);
        SaveHabitButton = view.findViewById(R.id.saveButton3);

        db = new HabitDatabaseHelper(getActivity());

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            EditHabitText.setText(task);

            if (task.length() > 0 ){
                SaveHabitButton.setEnabled(false);
            }

        }
        EditHabitText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    SaveHabitButton.setEnabled(false);
                    SaveHabitButton.setBackgroundColor(Color.GRAY);
                }else{
                    SaveHabitButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final boolean finalIsUpdate = isUpdate;
        SaveHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = EditHabitText.getText().toString();

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                    HabitViewModel.refreshData(db);

                } else {
                    HabitModel item = new HabitModel();
                    item.setTask(text);
                    db.insertTask(item);

                    // Notify LiveData that data has changed
                    HabitViewModel.refreshData(db);
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
