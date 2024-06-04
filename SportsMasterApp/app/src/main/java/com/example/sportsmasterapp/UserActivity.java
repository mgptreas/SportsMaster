package com.example.sportsmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvUsername, tvName, tvEmail, tvHeight, tvWeight, tvPoints, tvPremium;
    private Button btnToggle;
    private LinearLayout userInfoLayout;
    private RecyclerView rvExerciseStats;
    private ProgressBar loadingSpinner;
    private ExerciseStatAdapter exerciseStatAdapter;
    private boolean showingUserInfo = true;
    private ImageView ivHomeIcon; //Home icon to go back to home screen
    private ImageView ivLockIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        sessionManager = SessionManager.getInstance(this);

        tvUsername = findViewById(R.id.tv_username);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvHeight = findViewById(R.id.tv_height);
        tvWeight = findViewById(R.id.tv_weight);
        tvPoints = findViewById(R.id.tv_points);
        tvPremium = findViewById(R.id.tv_premium);
        btnToggle = findViewById(R.id.btn_toggle);
        userInfoLayout = findViewById(R.id.user_info_layout);
        rvExerciseStats = findViewById(R.id.rv_exercise_stats);
        loadingSpinner = findViewById(R.id.loading_spinner);
        ivHomeIcon = findViewById(R.id.iv_home_icon);
        ivLockIcon = findViewById(R.id.iv_lock_icon);


        rvExerciseStats.setLayoutManager(new LinearLayoutManager(this));
        exerciseStatAdapter = new ExerciseStatAdapter(new ArrayList<>());
        rvExerciseStats.setAdapter(exerciseStatAdapter);

        // Initial display (user info)
        displayUserInfo();

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingUserInfo) {
                    fetchAndDisplayExerciseStats();
                } else {
                    displayUserInfo();
                }
                showingUserInfo = !showingUserInfo;
            }
        });

        ivHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        ivLockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, UnlockedExercisesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayUserInfo() {
        User user = sessionManager.getUser();

        if (user != null) {
            tvUsername.setText("Username: " + user.getUsername());
            tvName.setText("Name: " + user.getName());
            tvEmail.setText("Email: " + user.getEmail());
            tvHeight.setText("Height: " + user.getHeight());
            tvWeight.setText("Weight: " + user.getWeight());
            tvPoints.setText("Points: " + user.getPoints());
            tvPremium.setText("Premium: " + user.isPremium());

            userInfoLayout.setVisibility(View.VISIBLE);
            rvExerciseStats.setVisibility(View.GONE);
            loadingSpinner.setVisibility(View.GONE);
            btnToggle.setText("Show Exercise Stats");
        }
    }

    private void fetchAndDisplayExerciseStats() {
        User user = sessionManager.getUser();
        if (user == null) {
            Toast.makeText(UserActivity.this, "No user found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        loadingSpinner.setVisibility(View.VISIBLE); // Show loading spinner
        userInfoLayout.setVisibility(View.GONE); // Hide user info
        rvExerciseStats.setVisibility(View.GONE); // Hide the RecyclerView during loading

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<ExerciseStat>> call = apiService.getUserExerciseStats(user.getUID());

        call.enqueue(new Callback<List<ExerciseStat>>() {
            @Override
            public void onResponse(Call<List<ExerciseStat>> call, Response<List<ExerciseStat>> response) {
                loadingSpinner.setVisibility(View.GONE); // Hide loading spinner on response

                if (response.isSuccessful()) {
                    List<ExerciseStat> stats = response.body();
                    if(stats != null){
                        displayExerciseStats(stats);
                        sessionManager.saveExerciseStats(stats);
                    }
                } else {
                    Toast.makeText(UserActivity.this, "Failed to fetch exercise stats", Toast.LENGTH_SHORT).show();
                    Log.e("UserActivity", "API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ExerciseStat>> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE); // Hide loading spinner on failure
                Toast.makeText(UserActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("UserActivity", "Network Error: " + t.getMessage());
            }
        });
        btnToggle.setText("Show User Info");
    }


    private void displayExerciseStats(List<ExerciseStat> stats) {
        exerciseStatAdapter.updateStats(stats); // Update the adapter's data
        userInfoLayout.setVisibility(View.GONE);
        rvExerciseStats.setVisibility(View.VISIBLE);
    }
}
