package com.example.yassinesaddikimeteoapp.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.IOException


class LocalMeteoActivity : AppCompatActivity() {

    val latitude = intent.getDoubleExtra("latitude", 0.0)
    val longitude = intent.getDoubleExtra("longitude", 0.0)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.meteomatics.com/")
        .client(createClient())
        .build()

    private val service = retrofit.create(WeatherApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentTemperature by remember { mutableStateOf<Double?>(null) }
            var minTemperature by remember { mutableStateOf<Double?>(null) }
            var maxTemperature by remember { mutableStateOf<Double?>(null) }
            var windSpeed by remember { mutableStateOf<Double?>(null) }
            var weatherSymbol by remember { mutableStateOf<Int?>(null) }
            var uvIndex by remember { mutableStateOf<Int?>(null) }

            LaunchedEffect(true) {
                fetchWeatherData()?.let { data ->
                    currentTemperature = extractValue(data, "t_2m:C")
                    minTemperature = extractValue(data, "t_min_2m_24h:C")
                    maxTemperature = extractValue(data, "t_max_2m_24h:C")
                    windSpeed = extractValue(data, "wind_gusts_10m_24h:ms")
                    weatherSymbol = extractValue(data, "weather_symbol_24h:idx")?.toInt()
                    uvIndex = extractValue(data, "uv:idx")?.toInt()
                }
            }

            MyApplicationTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        currentTemperature?.let { Text(text = "Température actuelle: $it°C") }
                        minTemperature?.let { Text(text = "Température minimale: $it°C") }
                        maxTemperature?.let { Text(text = "Température maximale: $it°C") }
                        windSpeed?.let { Text(text = "Vitesse du vent: $it m/s") }
                        weatherSymbol?.let { Text(text = "Symbole météo: $it") }
                        uvIndex?.let { Text(text = "Indice UV: $it") }
                    }
                }
            }
        }
    }

    private suspend fun fetchWeatherData(): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getWeatherData().execute()
                if (response.isSuccessful) {
                    JSONObject(response.body()?.string() ?: "{}")
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }

    private fun extractValue(data: JSONObject, parameter: String): Double? {
        val dataArray = data.getJSONArray("data")
        for (i in 0 until dataArray.length()) {
            val obj = dataArray.getJSONObject(i)
            if (obj.getString("parameter") == parameter) {
                val coordinatesArray = obj.getJSONArray("coordinates")
                val coordinateObj = coordinatesArray.getJSONObject(0)
                val datesArray = coordinateObj.getJSONArray("dates")
                val dateObj = datesArray.getJSONObject(0)
                return dateObj.getDouble("value")
            }
        }
        return null
    }

    interface WeatherApiService {
        @GET("2024-04-01T00:00:00Z/t_2m:C,t_min_2m_24h:C,t_max_2m_24h:C,wind_gusts_10m_24h:ms,weather_symbol_24h:idx,uv:idx/latitude,longitude/json")
        fun getWeatherData(): Call<ResponseBody>
    }

    private fun createClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", Credentials.basic("nknkj_etre_kjnk", "jYY3dcT77O"))
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }
}

