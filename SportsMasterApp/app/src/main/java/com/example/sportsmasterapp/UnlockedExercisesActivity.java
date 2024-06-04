package com.example.sportsmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnlockedExercisesActivity extends AppCompatActivity {

    private ImageView ivHomeIcon, ivUserIcon;
    private RecyclerView rvUnlockedExercises;
    private ProgressBar loadingSpinner;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlocked_exercises);

        sessionManager = SessionManager.getInstance(this);
        rvUnlockedExercises = findViewById(R.id.rv_unlocked_exercises);
        loadingSpinner = findViewById(R.id.loading_spinner);

        ivHomeIcon = findViewById(R.id.iv_home_icon); // Assuming you've used the standard layout
        ivUserIcon = findViewById(R.id.iv_user_icon);

        ivHomeIcon.setOnClickListener(v -> startActivity(new Intent(UnlockedExercisesActivity.this, HomeScreenActivity.class)));
        ivUserIcon.setOnClickListener(v -> startActivity(new Intent(UnlockedExercisesActivity.this, UserActivity.class)));

        // Fetch and display unlocked exercises on activity start
        fetchAndDisplayExercises();
    }

    private void fetchAndDisplayExercises() {
        loadingSpinner.setVisibility(View.VISIBLE);
        User user = sessionManager.getUser();
        if (user == null) {
            return; // Handle the case where the user is not logged in
        }

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Exercise>> call = apiService.getAllExercises(user.getUID());

        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                loadingSpinner.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Exercise> exercises = response.body();
                    if (exercises != null) {
                        // Organize exercises by sport
                        Map<String, List<Exercise>> exercisesBySport = new HashMap<>();
                        for (Exercise exercise : exercises) {
                            String sportName = exercise.getSportName();
                            exercisesBySport.computeIfAbsent(sportName, k -> new ArrayList<>()).add(exercise);
                        }

                        // Pass only the organized map to the adapter
                        UnlockedExercisesAdapter adapter = new UnlockedExercisesAdapter(exercisesBySport, UnlockedExercisesActivity.this::onExerciseClick);
                        rvUnlockedExercises.setAdapter(adapter);
                        rvUnlockedExercises.setLayoutManager(new LinearLayoutManager(UnlockedExercisesActivity.this));
                    }
                } else {
                    Toast.makeText(UnlockedExercisesActivity.this, "Failed to fetch exercises", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                loadingSpinner.setVisibility(View.GONE);
                Toast.makeText(UnlockedExercisesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("UnlockedExercisesActivity", "Network Error: " + t.getMessage());
            }
        });
    }


    private void onExerciseClick(Exercise exercise) {
        Intent intent = new Intent(UnlockedExercisesActivity.this, ExerciseInfoActivity.class);
        intent.putExtra("exercise", exercise);
        intent.putExtra("sourceActivity", UnlockedExercisesActivity.class.getSimpleName());
        startActivity(intent);
    }
}


