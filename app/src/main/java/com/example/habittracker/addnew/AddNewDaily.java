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

import com.example.habittracker.database.HealthDatabaseHelper;
import com.example.habittracker.R;
import com.example.habittracker.onDialogClose;
import com.example.habittracker.ui.daily.DailyModel;
import com.example.habittracker.ui.daily.DailyViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewDaily extends BottomSheetDialogFragment {

    private EditText EditDailyText;
    private Button SaveDailyButton;
    private HealthDatabaseHelper db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout for fragment
        View layout = inflater.inflate(R.layout.add_newdaily, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditDailyText = view.findViewById(R.id.addDailyText);
        SaveDailyButton = view.findViewById(R.id.saveButton2);

        db = new HealthDatabaseHelper(getActivity());

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            EditDailyText.setText(task);

            if (task.length() > 0 ){
                SaveDailyButton.setEnabled(false);
            }
        }

        // Changing text
        EditDailyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    SaveDailyButton.setEnabled(false);
                    SaveDailyButton.setBackgroundColor(Color.GRAY);
                }else{
                    SaveDailyButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        SaveDailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = EditDailyText.getText().toString();

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                } else {
                    DailyModel item = new DailyModel();
                    item.setTask(text);
                    item.setStatus(0);
                    db.insertTask(item);

                    // Refresh when data is changed
                    DailyViewModel.refreshData(db);
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
