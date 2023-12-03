package com.example.habittracker.ui.character;

        import android.content.Intent;
        import android.database.Cursor;
        import android.media.MediaPlayer;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.VideoView;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.Fragment;

        import com.example.habittracker.database.CharacterDatabaseHelper;
        import com.example.habittracker.MainActivity;
        import com.example.habittracker.ProgressUtil;
        import com.example.habittracker.R;
        import com.example.habittracker.databinding.CharacterMainBinding;

public class CharacterFragment extends Fragment {
    private CharacterMainBinding binding;
    private CharacterDatabaseHelper dbHelper;
    private int body = 1;
    private int hair = 1;
    private int pants = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        binding = CharacterMainBinding.inflate(inflater, container, false);
        dbHelper = new CharacterDatabaseHelper(requireContext());

        VideoView videoView = binding.videoView;

        // Get video
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.videocustom);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();

        // Video looper
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });

        videoView.start();
        // Layout
        videoView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView bodyView = binding.characterBody;
        ImageView hairView = binding.characterHair;
        ImageView pantsView = binding.characterPants;

        updateCharacterImage(body, bodyView, "body");
        updateCharacterImage(hair, hairView, "hair");
        updateCharacterImage(pants, pantsView, "pants");


        ImageButton nextBody = binding.nextBody;
        ImageButton previousBody = binding.previousBody;


        ImageButton nextHair = binding.nextHair;
        ImageButton previousHair = binding.previousHair;


        ImageButton nextPants = binding.nextPants;
        ImageButton previousPants = binding.previousPants;

        ImageButton saveButton = binding.characterSave;
        EditText textName = binding.textName;

        loadSavedCharacterData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCharacterToDatabase();

                // Start main activity
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtra("triggerMainLogic", true);
                startActivity(intent);
                requireActivity().finish();

                String name = textName.getText().toString();
                ProgressUtil.saveName(requireContext(), name);
            }
        });



        nextBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = (body % 5) + 1;
                updateCharacterImage(body, bodyView, "body");
            }
        });

        previousBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                body = ((body - 2 + 5) % 5) + 1;
                updateCharacterImage(body, bodyView, "body");
            }
        });

        nextHair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hair = (hair % 5) + 1;
                updateCharacterImage(hair, hairView, "hair");
            }
        });

        previousHair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hair = ((hair - 2 + 5) % 5) + 1;
                updateCharacterImage(hair, hairView, "hair");
            }
        });

        nextPants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pants = (pants % 5) + 1;
                updateCharacterImage(pants, pantsView, "pants");
            }
        });

        previousPants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pants = ((pants - 2 + 5) % 5) + 1;
                updateCharacterImage(pants, pantsView, "pants");
            }
        });

        return binding.getRoot();
    }

    private void updateCharacterImage(int item, ImageView imageView, String part) {
        int resId = getResources().getIdentifier(part + item, "drawable", requireActivity().getPackageName());
        imageView.setImageResource(resId);
    }

    private void loadSavedCharacterData() {
        CharacterDatabaseHelper dbHelper = new CharacterDatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getSavedCharacterData();

        if (cursor != null && cursor.moveToFirst()) {

            int savedBody = cursor.getInt(cursor.getColumnIndexOrThrow("body"));
            int savedHair = cursor.getInt(cursor.getColumnIndexOrThrow("hair"));
            int savedPants = cursor.getInt(cursor.getColumnIndexOrThrow("pants"));

            // Access main activity
            MainActivity mainActivity = (MainActivity) requireActivity();
            ImageView displayBody = mainActivity.findViewById(R.id.displayBody);
            ImageView displayHair = mainActivity.findViewById(R.id.displayHair);
            ImageView displayPants = mainActivity.findViewById(R.id.displayPants);

            // Update the image views
            if (displayBody != null && displayHair != null && displayPants != null) {
                updateCharacterImage(savedBody, displayBody, "body");
                updateCharacterImage(savedHair, displayHair, "hair");
                updateCharacterImage(savedPants, displayPants, "pants");
            }

            cursor.close();
        }
    }


    private void saveCharacterToDatabase() {
        // Save to database
        dbHelper.saveCharacterData(body, hair, pants);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show action bar after the fragment
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
    }
}

