package com.example.yassinesaddikimeteoapp.android


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val scope = rememberCoroutineScope()
                var inputText by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds 
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting("Bienvenue")
                        ButtonMeteoLocal(context = LocalContext.current)
                        InputField(
                            text = inputText,
                            onValueChange = { inputText = it }
                        )
                        ButtonValider(
                            context = LocalContext.current,
                            city = inputText
                        )
                        CityInputField(
                            context = LocalContext.current,
                            city = inputText
                        )
                    }
                }
            }
        }
    }



//-------------------------------------------


    suspend fun fetchCoordinates(cityName: String): Pair<Double, Double>? {
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
                    return@withContext Pair(lat, lon)
                } else {
                    // Aucune coordonnée trouvée //TODO
                    null
                }
            } else {
                // Réponse non réussie  //TODO
                null
            }
        }
    }


    //-------------------------------------------
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Box(
            modifier = Modifier.offset(y = (-70).dp),
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
    fun ButtonValider(context: Context, city: String) {
        Box(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val coordinates = fetchCoordinates(city)
                        if (coordinates != null) {
                            val date = Date()
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                            val formattedDate = dateFormat.format(date)
                            val latitude = coordinates.first
                            val longitude = coordinates.second

                            val intent = Intent(context, LocalMeteoPage::class.java)


                            intent.putExtra("latitude", latitude)
                            intent.putExtra("longitude", longitude)
                            intent.putExtra("date", formattedDate)

                            context.startActivity(intent)
                        } else {
                            Toast.makeText(
                                context,
                                "Coordonnées introuvable, essayer avec une ville Française",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                },
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


//---------------------------------------------------------------------------------------//


    @Composable
    fun CityInputField(context: Context, city: String) {
        val focusManager = LocalFocusManager.current
        val cityInput = remember { mutableStateOf("") }
        val cityList = remember { mutableStateOf(listOf<String>()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = cityInput.value,
                onValueChange = { cityInput.value = it },
                label = { Text("Entrer une ville préférée") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (cityInput.value.isNotBlank()) {
                            cityList.value = cityList.value + cityInput.value
                            cityInput.value = ""
                        }
                    }
                )
            )
            Button(
                onClick = {
                    if (cityInput.value.isNotBlank()) {
                        cityList.value = cityList.value + cityInput.value
                        cityInput.value = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cityList.value.take(5).forEach { city ->
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                val coordinates = fetchCoordinates(city)
                                if (coordinates != null) {
                                    val date = Date()
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                    val formattedDate = dateFormat.format(date)
                                    val latitude = coordinates.first
                                    val longitude = coordinates.second

                                    val intent = Intent(context, LocalMeteoPage::class.java)


                                    intent.putExtra("latitude", latitude)
                                    intent.putExtra("longitude", longitude)
                                    intent.putExtra("date", formattedDate)

                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Coordonnées introuvable, essayer avec une ville Française",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = city,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------//

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }
    interface LocationCallback {
        fun onLocationReceived(location: Pair<Double, Double>?)
    }

    private fun getUserLocation(callback: LocationCallback) {

        var locationManager: LocationManager

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_LOCATION
            )
        } else {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(locationResult: Location) {
                        val latitude = locationResult.latitude
                        val longitude = locationResult.longitude
                        callback.onLocationReceived(Pair(latitude, longitude))
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onProviderDisabled(provider: String) {}
                },
                null
            )
        }
    }



    //__________________________________________________________________________________





@Composable
    fun ButtonMeteoLocal(context: Context) {
        Box(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = {
                    getUserLocation(object : LocationCallback {
                        override fun onLocationReceived(location: Pair<Double, Double>?) {
                            location?.let { (latitude, longitude) ->
                                val date = Date()
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val formattedDate = dateFormat.format(date)

                                val intent = Intent(context, LocalMeteoPage::class.java)
                                intent.putExtra("latitude", latitude)
                                intent.putExtra("longitude", longitude)
                                intent.putExtra("date", formattedDate)

                                context.startActivity(intent)
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "Votre localisation est introuvable",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                },
                modifier = Modifier.padding(start = 8.dp)
                    .size(150.dp, 50.dp), // Modification de la taille du bouton
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE9AA30)
                ),
            ) {
                Text(
                    text = "Meteo chez moi",
                    color = Color.White,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                )
            }
        }
    }
}









