package com.example.yassinesaddikimeteoapp.android


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val scope = rememberCoroutineScope()
                var inputText by remember { mutableStateOf("") }
                var currentTemperature by remember { mutableStateOf<Double?>(null) }
                var minTemperature by remember { mutableStateOf<Double?>(null) }
                var maxTemperature by remember { mutableStateOf<Double?>(null) }
                var windSpeed by remember { mutableStateOf<Double?>(null) }
                var weatherSymbol by remember { mutableStateOf<Int?>(null) }
                var uvIndex by remember { mutableStateOf<Int?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting("Bienvenue")
                        InputField(
                            text = inputText,
                            onValueChange = { inputText = it }
                        )
                        ButtonValider(onClick = {
                            if (inputText.isNotEmpty()) {
                                scope.launch {
                                    val coordinates = fetchCoordinates(inputText)
                                    coordinates?.let { (latitude, longitude) ->
                                        val data = fetchWeatherData(latitude, longitude)
                                        data?.let {
                                            currentTemperature = extractValue(data, "t_2m:C")
                                            minTemperature = extractValue(data, "t_min_2m_24h:C")
                                            maxTemperature = extractValue(data, "t_max_2m_24h:C")
                                            windSpeed = extractValue(data, "wind_gusts_10m_24h:ms")
                                            weatherSymbol = extractValue(data, "weather_symbol_24h:idx")?.toInt()
                                            uvIndex = extractValue(data, "uv:idx")?.toInt()
                                        }
                                    }
                                }
                            }
                        })

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

    // Méthode pour récupérer les coordonnées de la ville
    private suspend fun fetchCoordinates(cityName: String): Pair<Double, Double>? {
        val url = "https://nominatim.openstreetmap.org/search?q=$cityName&format=json"
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val jsonData = response.body?.string()
                val jsonArray = JSONArray(jsonData)

                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val lat = jsonObject.getString("lat").toDouble()
                    val lon = jsonObject.getString("lon").toDouble()
                    Pair(lat, lon)
                } else {
                    // Return null if no coordinates are found
                    null
                }
            } else {
                // Return null if the response is not successful
                null
            }
        }
    }


    // Méthode pour récupérer les données météorologiques
    private suspend fun fetchWeatherData(latitude: Double, longitude: Double): JSONObject? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.meteomatics.com/")
            .client(createClient())
            .build()

        val service = retrofit.create(WeatherApiService::class.java)

        return withContext(Dispatchers.IO) {
            try {
                val response = service.getWeatherData(latitude, longitude).execute()
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

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Box(
            modifier = Modifier.offset(y = (-200).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                modifier = modifier,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InputField(
        text: String,
        onValueChange: (String) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
            OutlinedTextField(
                value = text,
                onValueChange = onValueChange,
                label = { Text("Chercher une ville...") },
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFD9D9D9), // Définir la couleur de fond de l'input
                    cursorColor = Color.Black, // Définir la couleur du curseur
                    focusedBorderColor = Color.Black, // Définir la couleur du contour lorsqu'il est sélectionné
                    unfocusedBorderColor = Color.Black // Définir la couleur du contour lorsqu'il n'est pas sélectionné
                ),
                textStyle = TextStyle(fontSize = 16.sp) // Définir la taille de la police du texte
            )
        }
    }

    @Composable
    fun ButtonValider(onClick: () -> Unit) {
        Box(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.padding(start = 8.dp)
                    .size(150.dp, 50.dp), // Modification de la taille du bouton
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF55ABFA)
                ),
            ) {
                Text(
                    text = "VALIDER",
                    color = Color.White,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                )
            }
        }
    }

    interface WeatherApiService {
        @GET("{latitude},{longitude}/t_2m:C,t_min_2m_24h:C,t_max_2m_24h:C,wind_gusts_10m_24h:ms,weather_symbol_24h:idx,uv:idx/2024-04-03T00:00:00Z/json")
        fun getWeatherData(@Path("latitude") latitude: Double, @Path("longitude") longitude: Double): Call<ResponseBody>
    }
}












