package com.example.yassinesaddikimeteoapp.android


import android.app.Application

@WeatherApp.HiltAndroidApp
class WeatherApp : Application() {
    annotation class HiltAndroidApp
}