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

        // Fetch exercise info
        Exercise exercise = getIntent().getParcelableExtra("exercise");
        displayExerciseInfo(exercise);

        SessionManager sessionManager = SessionManager.getInstance(this);
        User user = sessionManager.getUser(); // Fetch the user's information from the session

        btnUnlock.setOnClickListener(v -> {
            // Check if the user object is not null and has more than 5 coins
            if (user != null && user.getPoints() >= 5) {
                // Call the API to unlock the exercise
                ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                Call<Void> call = apiService.unlockExercise(user.getUID(), exercise.getEID());

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Exercise unlocked successfully
                            Toast.makeText(ExerciseInfoActivity.this, "Exercise unlocked successfully.", Toast.LENGTH_SHORT).show();

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

        // Display the fields
        displayFields(exercise);
    }

    private void displayFields(Exercise exercise) {
        TextView tvField1 = findViewById(R.id.tv_field1);
        TextView tvField2 = findViewById(R.id.tv_field2);
        TextView tvField3 = findViewById(R.id.tv_field3);
        TextView tvField4 = findViewById(R.id.tv_field4);
        TextView tvField5 = findViewById(R.id.tv_field5);

        tvField1.setText("Field1: " + exercise.getField1());
        tvField2.setText("Field2: " + exercise.getField2());
        tvField3.setText("Field3: " + exercise.getField3());
        tvField4.setText("Field4: " + exercise.getField4());
        tvField5.setText("Field5: " + exercise.getField5());
    }
}
