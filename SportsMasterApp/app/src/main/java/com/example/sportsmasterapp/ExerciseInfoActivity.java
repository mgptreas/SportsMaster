package com.example.sportsmasterapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
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
