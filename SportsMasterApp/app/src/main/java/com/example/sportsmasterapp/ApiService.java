package com.example.sportsmasterapp;

//Interface that defines the structure of the API endpoints

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/register_user/")
    Call<User> registerUser(@Body User user);

    @GET("/api/login_user/")
    Call<User> loginUser(@Query("username") String username, @Query("password") String password);
    @GET("api/get_user_exercise_stats/")
    Call<List<ExerciseStat>> getUserExerciseStats(@Query("uID") int userId);

    @GET("/api/check_unlocked/")
    Call<List<Integer>> getUnlockedExercises(@Query("user_id") int userId);

    @GET("api/get_exercises_with_sport/")
    Call<List<Exercise>> getAllExercises(@Query("uID") int userId);

    @GET("/api/get_sport_fields/")
    Call<Map<String, List<String>>> getSportFields(@Query("sport_name") String sportName);

    @GET("/api/select_workout/")
    Call<List<Exercise>> selectWorkout(
            @Query("uID") int userId,
            @Query("sport_name") String sportName,
            @Query("fields") String fields, // Comma-separated list of fields
            @Query("time") int timeAvailable // Time in minutes
    );

    @GET("/api/get_exercise_info/")
    Call<Exercise> getExerciseInfo(@Query("eID") int exerciseId);

    @POST("/api/unlock_exercise/")
    Call<Void> unlockExercise(@Body UnlockRequest unlockRequest);

    @POST("api/save_exercise_instance_stats/")
    Call<Void> sendExerciseStats(
            @Query("uID") int uID,
            @Query("eID") int eID,
            @Query("TOC") int toc,
            @Query("challenging") int challenging,
            @Query("feedback") int feedback
    );
}
