package com.example.yassinesaddikimeteoapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

class LocalMeteoPage : ComponentActivity() {

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
                val date = intent.getStringExtra("date")
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)

                if (date != null) {
                    fetchWeatherData(date, latitude, longitude)?.let { data ->
                        currentTemperature = extractValue(data, "t_2m:C")
                        minTemperature = extractValue(data, "t_min_2m_24h:C")
                        maxTemperature = extractValue(data, "t_max_2m_24h:C")
                        windSpeed = extractValue(data, "wind_gusts_10m_24h:ms")
                        weatherSymbol = extractValue(data, "weather_symbol_24h:idx")?.toInt()
                        uvIndex = extractValue(data, "uv:idx")?.toInt()
                    }
                }
            }

            MyApplicationTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val intent = intent
                    val latitude = intent.getDoubleExtra("latitude", 0.0)
                    val longitude = intent.getDoubleExtra("longitude", 0.0)
                    val date = intent.getStringExtra("date")

                    Column(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        currentTemperature?.let { currentTemp ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = "Température actuelle",
                                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = androidx.compose.ui.Modifier.padding(end = 8.dp))
                                Text(
                                    text = "Température actuelle: ${currentTemp}°C",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        minTemperature?.let { minTemp ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Température minimale",
                                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = androidx.compose.ui.Modifier.padding(end = 8.dp))
                                Text(
                                    text = "Température minimale: ${minTemp}°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        maxTemperature?.let { maxTemp ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Température maximale",
                                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = androidx.compose.ui.Modifier.padding(end = 8.dp))
                                Text(
                                    text = "Température maximale: ${maxTemp}°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        windSpeed?.let { wind ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Vitesse du vent",
                                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = androidx.compose.ui.Modifier.padding(end = 8.dp))
                                Text(
                                    text = "Vitesse du vent: ${wind} m/s",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        weatherSymbol?.let { symbol ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                /*Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Symbole météo",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )*/
                                Spacer(modifier = androidx.compose.ui.Modifier.padding(end = 8.dp))
                                Text(
                                    text = "Symbole météo: $symbol",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        uvIndex?.let { uv ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Indice UV ",
                                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = androidx.compose.ui.Modifier.padding(end = 8.dp))
                                Text(
                                    text = "Indice UV : $uv",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    private suspend fun fetchWeatherData(date: String, latitude: Double, longitude: Double): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getWeatherData(date, latitude, longitude).execute()
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
        @GET("{date}T00:00:00Z/t_2m:C,t_min_2m_24h:C,t_max_2m_24h:C,wind_gusts_10m_24h:ms,weather_symbol_24h:idx,uv:idx/{latitude},{longitude}/json")
        fun getWeatherData(
            @Path("date") date: String,
            @Path("latitude") latitude: Double,
            @Path("longitude") longitude: Double
        ): Call<ResponseBody>
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

