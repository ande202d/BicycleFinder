package com.example.bicyclefinder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RESTService {
    //BIKES
    @GET("Bicycles/id/{bikeId}")
    Call<Bike> getOneBike(@Path("bikeId") int bikeId);

    @GET("Bicycles/")
    Call<List<Bike>> getAllBikes();

    @GET("Bicycles/missing")
    Call<List<Bike>> getAllMissingBikes();

    @GET("Bicycles/found")
    Call<List<Bike>> getAllFoundBikes();

    @POST("Bicycles/")
    Call<Bike> postBike(@Body Bike bike);

    //------------------------------------------------
    //USERS
    @GET("Users/{id}")
    Call<User> getOneUser(@Path("id") int userId);
    //------------------------------------------------
}
