package com.example.sportsmasterapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldSelectActivity extends AppCompatActivity {

    private LinearLayout fieldCheckboxesContainer;
    private NumberPicker npHours, npMinutes;
    private Button btnGenerateWorkout;
    private String selectedSport;
    private List<CheckBox> fieldCheckboxes;

    private ImageView ivHomeIcon, ivUserIcon, ivLogoIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.field_select);

        // Initialize top icons
        ivHomeIcon = findViewById(R.id.iv_home_icon);
        ivUserIcon = findViewById(R.id.iv_user_icon);
        ivLogoIcon = findViewById(R.id.iv_logo);

        // Set onClickListener for top icons
        ivHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FieldSelectActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FieldSelectActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        fieldCheckboxesContainer = findViewById(R.id.field_checkboxes_container);
        npHours = findViewById(R.id.np_hours);
        npMinutes = findViewById(R.id.np_minutes);
        btnGenerateWorkout = findViewById(R.id.btn_generate_workout);
        fieldCheckboxes = new ArrayList<>();

        // Retrieve sport name from Intent
        selectedSport = getIntent().getStringExtra("sport_name");


        // Set up NumberPickers for time selection
        npHours.setMinValue(0);
        npHours.setMaxValue(23);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(3);
        npMinutes.setDisplayedValues(new String[]{"0", "15", "30", "45"});

        // Fetch sport fields from the API
        fetchSportFields();

        NumberPicker.OnValueChangeListener valueChangeListener = (picker, oldVal, newVal) -> validateGenerateButton();
        npHours.setOnValueChangedListener(valueChangeListener);
        npMinutes.setOnValueChangedListener(valueChangeListener);

        btnGenerateWorkout.setOnClickListener(v -> generateWorkout());
    }

    private void fetchSportFields() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<Map<String, List<String>>> call = apiService.getSportFields(selectedSport); // Changed return type

        call.enqueue(new Callback<Map<String, List<String>>>() { // Change to Callback<Map<String, List<String>>>
            @Override
            public void onResponse(Call<Map<String, List<String>>> call, Response<Map<String, List<String>>> response) {
                if (response.isSuccessful()) {
                    Map<String, List<String>> sportFields = response.body();
                    if (sportFields != null && sportFields.containsKey("fields")) {
                        List<String> fieldNames = sportFields.get("fields");
                        createFieldCheckboxes(fieldNames);
                        validateGenerateButton();
                    } else {
                        Toast.makeText(FieldSelectActivity.this, "No fields found for the sport", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FieldSelectActivity.this, "Failed to fetch sport fields", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<String>>> call, Throwable t) {
                Toast.makeText(FieldSelectActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("FieldSelectActivity", "Network Error: " + t.getMessage());
            }
        });
    }

    private void createFieldCheckboxes(List<String> fieldNames) {
        fieldCheckboxesContainer.removeAllViews(); // Clear previous checkboxes
        fieldCheckboxes.clear(); //Clear the list

        for (String fieldName : fieldNames) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(fieldName);
            checkBox.setTextColor(Color.WHITE); // Set text color (you might need to adjust based on your theme)
            fieldCheckboxes.add(checkBox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> validateGenerateButton());

            fieldCheckboxesContainer.addView(checkBox);
        }
    }

    private void validateGenerateButton() {
        boolean isAnyFieldChecked = false;
        for (CheckBox checkBox : fieldCheckboxes) {
            if (checkBox.isChecked()) {
                isAnyFieldChecked = true;
                break;
            }
        }

        boolean isTimeSelected = npHours.getValue() > 0 || npMinutes.getValue() > 0;

        btnGenerateWorkout.setEnabled(isAnyFieldChecked && isTimeSelected);
    }

    private void generateWorkout() {
        List<String> selectedFields = new ArrayList<>();
        for (CheckBox checkBox : fieldCheckboxes) {
            if (checkBox.isChecked()) {
                selectedFields.add(checkBox.getText().toString());
            }
        }

        int totalMinutes = npHours.getValue() * 60 + npMinutes.getValue() * 15;

        Intent intent = new Intent(FieldSelectActivity.this, WorkoutActivity.class);
        intent.putExtra("sport_name", selectedSport);
        intent.putStringArrayListExtra("fields", (ArrayList<String>) selectedFields);
        intent.putExtra("time", totalMinutes);
        startActivity(intent);
    }
}
