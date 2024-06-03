package com.example.sportsmasterapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SessionManager {
    private static final String SHARED_PREF_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_POINTS = "points";
    private static final String KEY_PREMIUM = "premium";
    private static final String KEY_EXERCISE_STATS = "exercise_stats";

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, user.getUID());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putFloat(KEY_HEIGHT, user.getHeight());
        editor.putFloat(KEY_WEIGHT, user.getWeight());
        editor.putInt(KEY_POINTS, user.getPoints());
        editor.putBoolean(KEY_PREMIUM, user.isPremium());
        editor.apply();
    }

    public User getUser() {
        int userID = sharedPreferences.getInt(KEY_USER_ID, -1);
        if (userID == -1) {
            return null; // No user stored
        }

        String username = sharedPreferences.getString(KEY_USERNAME, "");
        String name = sharedPreferences.getString(KEY_NAME, "");
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        float height = sharedPreferences.getFloat(KEY_HEIGHT, 0);
        float weight = sharedPreferences.getFloat(KEY_WEIGHT, 0);
        int points = sharedPreferences.getInt(KEY_POINTS, 0);
        boolean premium = sharedPreferences.getBoolean(KEY_PREMIUM, false);

        User user = new User();
        user.setUID(userID);
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setHeight(height);
        user.setWeight(weight);
        user.setPoints(points);
        user.setPremium(premium);

        return user;
    }

    public void saveExerciseStats(List<ExerciseStat> stats) {
        String exerciseStatsJson = gson.toJson(stats);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EXERCISE_STATS, exerciseStatsJson);
        editor.apply();
    }

    public List<ExerciseStat> getExerciseStats() {
        String exerciseStatsJson = sharedPreferences.getString(KEY_EXERCISE_STATS, null);
        if (exerciseStatsJson != null) {
            Type type = new TypeToken<List<ExerciseStat>>() {}.getType();
            return gson.fromJson(exerciseStatsJson, type);
        }
        return null;
    }

    public void clearUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
