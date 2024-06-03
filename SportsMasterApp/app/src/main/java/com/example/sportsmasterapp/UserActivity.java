package com.example.sportsmasterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvUsername, tvName, tvEmail, tvHeight, tvWeight, tvPoints, tvPremium;
    private Button btnToggle;
    private LinearLayout userInfoLayout;
    private HorizontalScrollView exerciseStatsLayout;
    private TableLayout tblExerciseStats;
    private boolean showingUserInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        sessionManager = SessionManager.getInstance(this); // Use singleton instance

        tvUsername = findViewById(R.id.tv_username);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvHeight = findViewById(R.id.tv_height);
        tvWeight = findViewById(R.id.tv_weight);
        tvPoints = findViewById(R.id.tv_points);
        tvPremium = findViewById(R.id.tv_premium);
        btnToggle = findViewById(R.id.btn_toggle);
        userInfoLayout = findViewById(R.id.user_info_layout);
        exerciseStatsLayout = findViewById(R.id.exercise_stats_layout);
        tblExerciseStats = findViewById(R.id.tbl_exercise_stats);

        displayUserInfo();

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingUserInfo) {
                    displayExerciseStats();
                } else {
                    displayUserInfo();
                }
                showingUserInfo = !showingUserInfo;
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
            exerciseStatsLayout.setVisibility(View.GONE);
            btnToggle.setText("Show Exercise Stats");
        }
    }

    private void displayExerciseStats() {
        List<ExerciseStat> stats = sessionManager.getExerciseStats();

        if (stats != null && !stats.isEmpty()) {
            tblExerciseStats.removeAllViews();

            // Add table header
            TableRow headerRow = new TableRow(this);
            addTextToRow(headerRow, "Name");
            addTextToRow(headerRow, "Description");
            addTextToRow(headerRow, "CoT");
            addTextToRow(headerRow, "avgTOC");
            addTextToRow(headerRow, "avgChallenging");
            addTextToRow(headerRow, "avgFeedback");
            tblExerciseStats.addView(headerRow);

            // Add exercise stats
            for (ExerciseStat stat : stats) {
                TableRow row = new TableRow(this);
                addTextToRow(row, stat.getExerciseName());
                addTextToRow(row, stat.getDescription());
                addTextToRow(row, String.valueOf(stat.getCoT()));
                addTextToRow(row, String.valueOf(stat.getAvgTOC()));
                addTextToRow(row, String.valueOf(stat.getAvgChallenging()));
                addTextToRow(row, String.valueOf(stat.getAvgFeedback()));
                tblExerciseStats.addView(row);
            }

            userInfoLayout.setVisibility(View.GONE);
            exerciseStatsLayout.setVisibility(View.VISIBLE);
            btnToggle.setText("Show User Info");
        }
    }

    private void addTextToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setPadding(8, 8, 8, 8);
        row.addView(textView);
    }
}
