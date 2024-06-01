package com.example.sportsmasterapp;

//Interface that defines the structure of the API endpoints


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("register_user/")
    Call<UserResponse> registerUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("name") String name,
            @Field("height") float height,
            @Field("weight") float weight,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login_user/")
    Call<UserResponse> loginUser(
            @Field("username") String username,
            @Field("password") String password
    );
}
