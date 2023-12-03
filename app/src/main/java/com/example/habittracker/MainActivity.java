package com.example.habittracker;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.habittracker.addnew.AddNewHabit;
import com.example.habittracker.addnew.AddNewTask;
import com.example.habittracker.database.CharacterDatabaseHelper;
import com.example.habittracker.databinding.ActivityMainBinding;
import com.example.habittracker.ui.character.CharacterFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements onDialogClose {

    private ActivityMainBinding binding;
    private CharacterDatabaseHelper characterHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Color changer for default status bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
            window.setNavigationBarColor(getResources().getColor(android.R.color.black));
        }

        // Navigation for Customization and the Main logic
        if (getIntent().getBooleanExtra("triggerMainLogic", false)) {
            mainLogic();
        } else {
            if (ProgressUtil.getCustomized(this) == false) {
                setContentView(R.layout.character_main);
                showCharacterCustomization();
            } else {
                mainLogic();
            }
        }
    }

    private void showCharacterCustomization() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.character_container, new CharacterFragment())
                .commit();
    }

    public void mainLogic(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setting up the Navigation bars
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        View customActionBarView = LayoutInflater.from(this).inflate(
                R.layout.custom_action_bar, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM
            );
            actionBar.setCustomView(customActionBarView);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        NavigationUI.setupWithNavController(navView, navController);

        // Navigation Bar text changer
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            TextView titleTextView = customActionBarView.findViewById(R.id.custom_title_textview);
            if (titleTextView != null) {
                titleTextView.setText(destination.getLabel());
            }
        });

        // Initializations
        ImageView displayBody = findViewById(R.id.displayBody);
        ImageView displayHair = findViewById(R.id.displayHair);
        ImageView displayPants = findViewById(R.id.displayPants);
        TextView nameView = findViewById(R.id.nameView);

        String name = ProgressUtil.getName(this);
        nameView.setText(name);
        ProgressUtil.saveCustomized(this, true);

        // Character Initialization
        characterHelper = new CharacterDatabaseHelper(this);
        Cursor cursor = characterHelper.getSavedCharacterData();

        int savedBody = cursor.getInt(cursor.getColumnIndexOrThrow("body"));
        int savedHair = cursor.getInt(cursor.getColumnIndexOrThrow("hair"));
        int savedPants = cursor.getInt(cursor.getColumnIndexOrThrow("pants"));

        if (displayBody != null && displayHair != null && displayPants != null) {
            updateCharacterImage(savedBody, displayBody, "body");
            updateCharacterImage(savedHair, displayHair, "hair");
            updateCharacterImage(savedPants, displayPants, "pants");
        }

        cursor.close();

        // Advancement Initialization
        String advancementImage = ProgressUtil.getAdvancement(this);
        if(advancementImage != null){
            ImageView advanceView = findViewById(R.id.advancementItem);
            int resId = getResources().getIdentifier(advancementImage, "drawable", getPackageName());
            advanceView.setImageResource(resId);
        }

        // Progress bar Initializations
        boolean isHealthInitialized = ProgressUtil.isHealthInitialized(this);
        if (!isHealthInitialized) {
            ProgressBar healthBar = findViewById(R.id.progressBar3);
            healthBar.setProgress(100);
            ProgressUtil.healthProgress(this, 100);
            ProgressUtil.setHealthInitialized(this, true);
        } else {
            ProgressBar healthBar = findViewById(R.id.progressBar3);
            int savedHealthProgress = ProgressUtil.getHealthProgress(this);
            healthBar.setProgress(savedHealthProgress);
        }

        ProgressBar exp_bar = findViewById(R.id.progressBar);
        int exp_progress = ProgressUtil.getExpProgress(this);
        exp_bar.setProgress(exp_progress);

        ProgressBar rank_bar = findViewById(R.id.progressBar2);
        int rank_progress = ProgressUtil.getRankProgress(this);
        rank_bar.setMax(ProgressUtil.getMaxProgressFromPreferences(this));
        rank_bar.setProgress(rank_progress);

        // Text View Initializations
        TextView healthTextView = findViewById(R.id.healthProgres);
        String saved_health = ProgressUtil.getSavedText(this);
        if(saved_health == ""){
            healthTextView.setText("100/100");
        } else {
            healthTextView.setText(saved_health);
        }

        TextView expTextView = findViewById(R.id.expProgres);
        String saved_exp = ProgressUtil.getSavedText2(this);
        if(saved_exp == ""){
            expTextView.setText("0/100");
        } else {
            expTextView.setText(saved_exp);
        }

        TextView rankTextView = findViewById(R.id.dailyProgres);
        if(ProgressUtil.getSavedText3(this) == ""){
            rankTextView.setText("Beginner");
            ProgressUtil.saveText3(this, "Beginner");
        } else {
            String saved_rank = ProgressUtil.getSavedText3(this);
            Log.d("wtd", "" + saved_rank);
            rankTextView.setText(saved_rank);
        }

        // Adding new list
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_dashboard) {
                floatingActionButton.setVisibility(View.INVISIBLE);
            } else {
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentFragmentId = navController.getCurrentDestination().getId();

                if (currentFragmentId == R.id.navigation_home) {
                    AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
                } else if (currentFragmentId == R.id.navigation_dashboard) {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                } else if (currentFragmentId ==R.id.navigation_notifications){
                    AddNewHabit.newInstance().show(getSupportFragmentManager(), AddNewHabit.TAG);
                }
            }
        });

    }
    private void updateCharacterImage(int item, ImageView imageView, String part) {
        int charID = getResources().getIdentifier(part + item, "drawable", getPackageName());
        imageView.setImageResource(charID);
    }

    public void updateExpBar(int increment) {
        ProgressBar expBar = findViewById(R.id.progressBar);
        TextView expText = findViewById(R.id.expProgres);



        if (expBar != null) {
            int currentProgress = expBar.getProgress();
            if(increment == -10 && currentProgress == 0) {
                increment = 0;
            }
            int newProgress = currentProgress + increment;

            int maxProgress = expBar.getMax();
            newProgress = Math.min(newProgress, maxProgress);

            if (newProgress == maxProgress) {
                newProgress = 0;
                updateRankBar(1);
                updateAdvancement();
            }

            ProgressUtil.expProgress(this, newProgress);
            expBar.setProgress(newProgress);

            String progressText = String.format("%d/%d", newProgress, maxProgress);
            ProgressUtil.saveText2(this, progressText);
            expText.setText(progressText);
        }
    }

    public void updateRankBar(int increment) {
        ProgressBar rankBar = findViewById(R.id.progressBar2);
        TextView rankText = findViewById(R.id.dailyProgres);

        if (rankBar != null) {
            if(ProgressUtil.getRankProgress(this) >= 0){

                int currentProgress = ProgressUtil.getRankProgress(this);
                int maxProgress = ProgressUtil.getMaxProgressFromPreferences(this);
                int newProgress = currentProgress + increment;
                String rankLabel = new String();

                if (newProgress >= maxProgress) {
                    newProgress = 0;
                    maxProgress += 10;
                    int newMaxProgress = maxProgress;
                    ProgressUtil.saveMaxProgressToPreferences(this, newMaxProgress);
                    rankBar.setMax(newMaxProgress);
                }

                if (maxProgress == 5){
                    rankLabel = "Beginner";
                } else if (maxProgress == 10) {
                    rankLabel = "Novice";
                } else if (maxProgress == 15) {
                    rankLabel = "Knight";
                } else if (maxProgress == 20){
                    rankLabel = "Gold Knight";
                }

                if (newProgress <= 0){

                    newProgress = 0;
                    maxProgress -= 5;
                    int newMaxProgress = maxProgress;
                    ProgressUtil.saveMaxProgressToPreferences(this, newMaxProgress);
                    rankBar.setMax(newMaxProgress);

                    if (maxProgress == 5){
                        rankLabel = "Beginner";
                    } else if (maxProgress == 10) {
                        rankLabel = "Novice";
                    } else if (maxProgress == 15) {
                        rankLabel = "Knight";
                    } else if (maxProgress == 20){
                        rankLabel = "Gold Knight";
                    }
                }

                ProgressUtil.rankProgress(this, newProgress);
                rankBar.setProgress(newProgress);
                String progressText = rankLabel;
                ProgressUtil.saveText3(this, progressText);
                rankText.setText(progressText);
                updateAdvancement();
            }



        }
    }

    public void updateAdvancement(){
        ImageView advanceView = findViewById(R.id.advancementItem);
        String progressText = ProgressUtil.getSavedText3(this);
        if (progressText == "Beginner"){
            advanceView.setImageResource(0);
            ProgressUtil.saveAdvancement(this, null);
        }
        if (progressText == "Novice"){
            advanceView.setImageResource(R.drawable.armor1);
            ProgressUtil.saveAdvancement(this, "armor1");
        } else if (progressText == "Knight") {
            advanceView.setImageResource(R.drawable.armor2);
            ProgressUtil.saveAdvancement(this, "armor2");
        } else if (progressText == "Gold Knight") {
            advanceView.setImageResource(R.drawable.armor3);
            ProgressUtil.saveAdvancement(this, "armor3");
        }
    }



    public void healthUpdate(int increment, TextView healthTextView) {

        ProgressBar progressBar = findViewById(R.id.progressBar3);

        if (progressBar != null && healthTextView != null && progressBar.getProgress() != 0) {
            int currentProgress = progressBar.getProgress();
            int newProgress = currentProgress + increment;

            int maxProgress = progressBar.getMax();
            newProgress = Math.min(newProgress, maxProgress);

            ProgressUtil.healthProgress(this, newProgress);
            progressBar.setProgress(newProgress);
            String progressText = String.format("%d/%d", newProgress, maxProgress);
            ProgressUtil.saveText(this, progressText);
            healthTextView.setText(progressText);
        }
        if (progressBar.getProgress() == 0){
            int newProgress = 100;

            int maxProgress = progressBar.getMax();
            newProgress = Math.min(newProgress, maxProgress);

            ProgressUtil.healthProgress(this, newProgress);
            progressBar.setProgress(newProgress);
            String progressText = String.format("%d/%d", newProgress, maxProgress);
            ProgressUtil.saveText(this, progressText);
            healthTextView.setText(progressText);



            updateRankBar(-5);

        }
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
    }
}
