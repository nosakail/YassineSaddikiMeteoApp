package com.example.yassinesaddikimeteoapp.android.repository

import com.example.yassinesaddikimeteoapp.android.model.WeatherApiResponse
import com.example.yassinesaddikimeteoapp.android.networking.NetworkingService
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val networkingService: NetworkingService) {

    suspend fun fetchWeather(long: String, lat: String): WeatherApiResponse =
        networkingService.fetchWeather(lon = long, lat = lat)

}