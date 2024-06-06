package com.example.sportsmasterapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseInfoActivity extends AppCompatActivity {

    private TextView tvExerciseName, tvExerciseDescription, tvExerciseDifficulty, tvExerciseTOC;
    private VideoView exerciseVideo;
    private Button btnUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_info);

        // Initialize views
        tvExerciseName = findViewById(R.id.tv_exercise_name);
        tvExerciseDescription = findViewById(R.id.tv_exercise_description);
        exerciseVideo = findViewById(R.id.video_view);
        tvExerciseDifficulty = findViewById(R.id.tv_exercise_difficulty);
        tvExerciseTOC = findViewById(R.id.tv_exercise_toc);
        btnUnlock = findViewById(R.id.btn_unlock);

        // Fetch exercise info from intent
        Exercise exercise = getIntent().getParcelableExtra("exercise");
        boolean isUnlocked = getIntent().getBooleanExtra("isUnlocked", false);
        exercise.setIsUnlocked(isUnlocked);

        // Call the API to get full exercise details
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Exercise> call = apiService.getExerciseInfo(exercise.getEID());

        call.enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful()) {
                    Exercise detailedExercise = response.body();
                    detailedExercise.setIsUnlocked(isUnlocked); // Ensure isUnlocked is set correctly
                    displayExerciseInfo(detailedExercise);
                } else {
                    Toast.makeText(ExerciseInfoActivity.this, "Failed to load exercise details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                Toast.makeText(ExerciseInfoActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("ExerciseInfoActivity", "Network Error: " + t.getMessage());
            }
        });

        SessionManager sessionManager = SessionManager.getInstance(this);
        User user = sessionManager.getUser(); // Fetch the user's information from the session

        btnUnlock.setOnClickListener(v -> {
            // Check if the user object is not null and has more than 5 coins
            if (user != null && user.getPoints() >= 5) {
                // Call the API to unlock the exercise
                ApiService apiServiceUnlock = ApiClient.getRetrofitInstance().create(ApiService.class);
                UnlockRequest unlockRequest = new UnlockRequest(user.getUID(), exercise.getEID());
                Call<Void> callUnlock = apiServiceUnlock.unlockExercise(unlockRequest);

                callUnlock.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Exercise unlocked successfully
                            Toast.makeText(ExerciseInfoActivity.this, "Exercise unlocked successfully.", Toast.LENGTH_SHORT).show();

                            user.setPoints(user.getPoints() - 5);
                            sessionManager.saveUser(user);

                            // Navigate to HomeScreenActivity
                            Intent intent = new Intent(ExerciseInfoActivity.this, HomeScreenActivity.class);
                            startActivity(intent);
                            finish(); // Finish current activity
                        } else {
                            // Failed to unlock exercise
                            Toast.makeText(ExerciseInfoActivity.this, "Failed to unlock exercise.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Network error
                        Toast.makeText(ExerciseInfoActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        Log.e("ExerciseInfoActivity", "Network Error: " + t.getMessage());
                    }
                });
            } else {
                // Insufficient points or user is not logged in
                Toast.makeText(ExerciseInfoActivity.this, "Please log in or earn more points to unlock the exercise.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayExerciseInfo(Exercise exercise) {
        tvExerciseName.setText(exercise.getName());
        tvExerciseDescription.setText(exercise.getDescription());

        Log.d("ExerciseInfoActivity", "Is Unlocked: " + exercise.getIsUnlocked());

        if (exercise.getIsUnlocked()) {
            String videoUrl = exercise.getVideo();
            Uri uri = Uri.parse(videoUrl);
            exerciseVideo.setVideoURI(uri);
            exerciseVideo.start();
            tvExerciseDescription.setVisibility(View.VISIBLE);
            exerciseVideo.setVisibility(View.VISIBLE);
            btnUnlock.setVisibility(View.GONE);
        } else {
            tvExerciseDescription.setVisibility(View.GONE);
            exerciseVideo.setVisibility(View.GONE);
            btnUnlock.setVisibility(View.VISIBLE);
        }

        tvExerciseDifficulty.setText("Difficulty: " + exercise.getDifficulty());
        tvExerciseTOC.setText("TOC: " + exercise.getTOC());

        displayFields(exercise);
    }

    private void displayFields(Exercise exercise) {
        TextView tvField1 = findViewById(R.id.tv_field1);
        TextView tvField2 = findViewById(R.id.tv_field2);
        TextView tvField3 = findViewById(R.id.tv_field3);
        TextView tvField4 = findViewById(R.id.tv_field4);
        TextView tvField5 = findViewById(R.id.tv_field5);

        Map<String, Integer> fields = exercise.getFields();

        if (fields != null) {
            List<String> fieldNames = new ArrayList<>(fields.keySet());
            List<Integer> fieldValues = new ArrayList<>(fields.values());

            if (fieldNames.size() > 0) {
                tvField1.setText(fieldNames.get(0) + ": " + fieldValues.get(0));
            }
            if (fieldNames.size() > 1) {
                tvField2.setText(fieldNames.get(1) + ": " + fieldValues.get(1));
            }
            if (fieldNames.size() > 2) {
                tvField3.setText(fieldNames.get(2) + ": " + fieldValues.get(2));
            }
            if (fieldNames.size() > 3) {
                tvField4.setText(fieldNames.get(3) + ": " + fieldValues.get(3));
            }
            if (fieldNames.size() > 4) {
                tvField5.setText(fieldNames.get(4) + ": " + fieldValues.get(4));
            }
        }
    }
}
