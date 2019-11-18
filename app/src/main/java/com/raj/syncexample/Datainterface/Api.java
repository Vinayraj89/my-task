package com.raj.syncexample.Datainterface;

import com.raj.syncexample.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by k-bridge on 16/10/19.
 */

public interface Api {


    @GET("2.5/weather?")
    Call<ApiResponse> FetchData(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String appid);

}
