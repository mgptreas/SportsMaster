package com.example.sportsmasterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeScreenActivity extends AppCompatActivity {

    private ImageView ivUserIcon,ivLockIcon;
    private LinearLayout llBasketball;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        ivUserIcon = findViewById(R.id.iv_user_icon);
        ivLockIcon = findViewById(R.id.iv_lock_icon);
        llBasketball = findViewById(R.id.ll_basketball);

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

        llBasketball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, FieldSelectActivity.class);
                intent.putExtra("sport_name", "Basketball");
                startActivity(intent);
            }
        });
    }
}