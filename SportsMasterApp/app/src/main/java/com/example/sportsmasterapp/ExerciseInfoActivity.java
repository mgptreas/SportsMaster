package com.example.sportsmasterapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.Util;

public class ExerciseInfoActivity extends AppCompatActivity {

    private TextView tvExerciseName, tvExerciseDescription, tvExerciseDifficulty, tvExerciseTOC;
    private Button btnUnlock;
    private StyledPlayerView playerView;
    private ExoPlayer exoPlayer;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_info);

        // Initialize views
        tvExerciseName = findViewById(R.id.tv_exercise_name);
        tvExerciseDescription = findViewById(R.id.tv_exercise_description);
        playerView = findViewById(R.id.video_view);
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
            String fileId = extractGoogleDriveFileId(videoUrl);

            // Test with a known working URL
            String directDownloadUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"; // Known working URL

            if (fileId != null) {
                directDownloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
            }

            Log.d("ExerciseInfoActivity", "Playing video from URL: " + directDownloadUrl);

            MediaItem mediaItem = MediaItem.fromUri(directDownloadUrl);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();

            tvExerciseDescription.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.VISIBLE);
            btnUnlock.setVisibility(View.GONE);
        } else {
            tvExerciseDescription.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
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

    private String extractGoogleDriveFileId(String url) {
        String pattern = "https://drive.google.com/file/d/(.*?)/view.*?";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(exoPlayer);

            // Adding debug listeners
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.e("ExoPlayer Error", "Player error: " + error.getMessage());
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {
                        case Player.STATE_BUFFERING:
                            Log.d("ExoPlayer", "Buffering");
                            break;
                        case Player.STATE_READY:
                            Log.d("ExoPlayer", "Ready to play");
                            break;
                        case Player.STATE_ENDED:
                            Log.d("ExoPlayer", "Playback ended");
                            break;
                        case Player.STATE_IDLE:
                            Log.d("ExoPlayer", "Idle");
                            break;
                    }
                }
            });
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
