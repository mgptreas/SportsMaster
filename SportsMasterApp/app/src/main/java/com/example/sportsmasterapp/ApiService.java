package com.example.sportsmasterapp;

//Interface that defines the structure of the API endpoints

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/register_user/")
    Call<User> registerUser(@Body User user);

    @GET("/api/login_user/")
    Call<User> loginUser(@Query("username") String username, @Query("password") String password);
    @GET("api/get-user-exercise-stats/")
    Call<List<ExerciseStat>> getUserExerciseStats(@Query("uID") int userId);
}
