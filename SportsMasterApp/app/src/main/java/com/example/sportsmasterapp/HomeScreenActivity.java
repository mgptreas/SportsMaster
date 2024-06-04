package com.example.sportsmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeScreenActivity extends AppCompatActivity {

    private ImageView ivUserIcon,ivLockIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        ivUserIcon = findViewById(R.id.iv_user_icon);
        ivLockIcon = findViewById(R.id.iv_lock_icon);

        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        ivLockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, UnlockedExercisesActivity.class);
                startActivity(intent);
            }
        });
    }
}