package com.example.sportsmasterapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutActivity extends AppCompatActivity {

    private ImageView ivHomeIcon, ivUserIcon, ivLogoIcon;
    private TextView tvStopwatch;
    private Button btnEndWorkout;
    private LinearLayout llExercises;
    private List<Exercise> exercises;
    private int currentExerciseIndex = -1;
    private long startTime, timeInMilliseconds = 0L;
    private Handler handler;
    private boolean isRunning = false;

    private Map<Integer, ExerciseStats> exerciseStatsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout);

        // Initialize top icons
        ivHomeIcon = findViewById(R.id.iv_home_icon);
        ivUserIcon = findViewById(R.id.iv_user_icon);
        ivLogoIcon = findViewById(R.id.iv_logo);

        // Set onClickListener for top icons
        ivHomeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        });

        ivUserIcon.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutActivity.this, UserActivity.class);
            startActivity(intent);
        });

        tvStopwatch = findViewById(R.id.tv_stopwatch);
        btnEndWorkout = findViewById(R.id.btn_end_workout);
        llExercises = findViewById(R.id.ll_exercises);

        btnEndWorkout.setOnClickListener(v -> endWorkout());

        // Get data from Intent
        Intent intent = getIntent();
        String sportName = intent.getStringExtra("sport_name");
        ArrayList<String> fields = intent.getStringArrayListExtra("fields");
        int time = intent.getIntExtra("time", 0);
        int userId = SessionManager.getInstance(this).getUser().getUID();

        // Fetch exercises
        fetchExercises(userId, sportName, fields, time);

        handler = new Handler();
    }

    private void fetchExercises(int userId, String sportName, ArrayList<String> fields, int time) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        String fieldsStr = String.join(",", fields);
        Call<List<Exercise>> call = apiService.selectWorkout(userId, sportName, fieldsStr, time);

        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful()) {
                    exercises = response.body();
                    displayExercises();
                } else {
                    Toast.makeText(WorkoutActivity.this, "Failed to fetch exercises", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Toast.makeText(WorkoutActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("WorkoutActivity", "Network Error: " + t.getMessage());
            }
        });
    }

    private void displayExercises() {
        llExercises.removeAllViews();

        for (int i = 0; i < exercises.size(); i++) {
            Exercise exercise = exercises.get(i);
            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_item, llExercises, false);
            TextView tvExerciseName = exerciseView.findViewById(R.id.tv_exercise_name);
            Button btnStartStop = exerciseView.findViewById(R.id.btn_start_stop);

            tvExerciseName.setText(exercise.getDescription());
            btnStartStop.setText(i == 0 ? "Start" : "Locked");
            btnStartStop.setEnabled(i == 0);

            int index = i;
            btnStartStop.setOnClickListener(v -> {
                if (isRunning) {
                    stopExercise(index, btnStartStop);
                } else {
                    startExercise(index, btnStartStop);
                }
            });

            llExercises.addView(exerciseView);
        }
    }

    private void startExercise(int index, Button btnStartStop) {
        currentExerciseIndex = index;
        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimer, 0);
        isRunning = true;
        btnStartStop.setText("Stop");
    }

    private void stopExercise(int index, Button btnStartStop) {
        timeInMilliseconds += System.currentTimeMillis() - startTime;
        handler.removeCallbacks(updateTimer);
        isRunning = false;
        btnStartStop.setText("Start");

        // Show feedback dialog
        showFeedbackDialog(index, btnStartStop);
    }

    private void showFeedbackDialog(int index, Button btnStartStop) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate the Exercise");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        NumberPicker npChallenging = dialogView.findViewById(R.id.np_challenging);
        NumberPicker npFeedback = dialogView.findViewById(R.id.np_feedback);

        npChallenging.setMinValue(0);
        npChallenging.setMaxValue(10);
        npFeedback.setMinValue(0);
        npFeedback.setMaxValue(10);

        builder.setView(dialogView);
        builder.setPositiveButton("Submit", (dialog, which) -> {
            int challenging = npChallenging.getValue();
            int feedback = npFeedback.getValue();

            exerciseStatsMap.put(index, new ExerciseStats(timeInMilliseconds, challenging, feedback, System.currentTimeMillis()));

            btnStartStop.setEnabled(false);
            if (index + 1 < exercises.size()) {
                View nextExerciseView = llExercises.getChildAt(index + 1);
                Button nextBtnStartStop = nextExerciseView.findViewById(R.id.btn_start_stop);
                nextBtnStartStop.setText("Start");
                nextBtnStartStop.setEnabled(true);
            } else {
                btnEndWorkout.setEnabled(true);
            }

            timeInMilliseconds = 0;
            tvStopwatch.setText("00:00:00");
        });


        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(android.R.color.black));
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long seconds = (elapsedTime / 1000) % 60;
            long minutes = (elapsedTime / (1000 * 60)) % 60;
            long hours = (elapsedTime / (1000 * 60 * 60)) % 24;

            tvStopwatch.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };

    private void endWorkout() {
        Intent intent = new Intent(WorkoutActivity.this, WorkoutRecapActivity.class);
        intent.putExtra("exercise_stats", new ArrayList<>(exerciseStatsMap.values()));
        startActivity(intent);
    }
}

class ExerciseStats {
    private long duration;
    private int challenging;
    private int feedback;
    private long timestamp;

    public ExerciseStats(long duration, int challenging, int feedback, long timestamp) {
        this.duration = duration;
        this.challenging = challenging;
        this.feedback = feedback;
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public int getChallenging() {
        return challenging;
    }

    public int getFeedback() {
        return feedback;
    }

    public long getTimestamp() {
        return timestamp;
    }
}