package com.example.yassinesaddikimeteoapp.android.networking

import com.example.yassinesaddikimeteoapp.android.model.WeatherApiResponse
import com.example.yassinesaddikimeteoapp.android.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkingService {

    @GET("data/2.5/onecall?exclude=minutely,hourly,alerts&lang=en&units=imperial")
    suspend fun fetchWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = Constants.APP_ID
    ): WeatherApiResponse

}