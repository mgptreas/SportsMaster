package com.example.sportsmasterapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseInfoActivity extends AppCompatActivity {

    private TextView tvExerciseName, tvExerciseDescription, tvExerciseDifficulty, tvExerciseTOC;
    private VideoView  ExerciseVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_info);

        // Initialize views
        tvExerciseName = findViewById(R.id.tv_exercise_name);
        tvExerciseDescription = findViewById(R.id.tv_exercise_description);
        ExerciseVideo = findViewById(R.id.video_view);
        tvExerciseDifficulty = findViewById(R.id.tv_exercise_difficulty);
        tvExerciseTOC = findViewById(R.id.tv_exercise_toc);

        // Fetch exercise info
        int exerciseId = getIntent().getIntExtra("exercise_id", -1);
        fetchExerciseInfo(exerciseId);
    }

    private void fetchExerciseInfo(int exerciseId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Exercise> call = apiService.getExerciseInfo(exerciseId); // Assuming your Retrofit service returns Exercise objects

        call.enqueue(new Callback<Exercise>() {
            @Override
            public void onResponse(Call<Exercise> call, Response<Exercise> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Exercise exercise = response.body();
                    displayExerciseInfo(exercise);
                }
            }

            @Override
            public void onFailure(Call<Exercise> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void displayExerciseInfo(Exercise exercise) {
        tvExerciseName.setText(exercise.getName());
        tvExerciseDescription.setText(exercise.getDescription());
        String videoUrl = exercise.getVideo();
        Uri uri = Uri.parse(videoUrl);
        ExerciseVideo.setVideoURI(uri);
        ExerciseVideo.start();
        tvExerciseDifficulty.setText("Difficulty: " + exercise.getDifficulty());
        tvExerciseTOC.setText("TOC: " + exercise.getTOC());
    }
}
