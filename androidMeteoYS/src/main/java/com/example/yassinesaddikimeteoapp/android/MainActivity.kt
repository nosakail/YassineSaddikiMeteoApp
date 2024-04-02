package com.example.yassinesaddikimeteoapp.android


import android.content.Intent
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
import androidx.compose.material.icons.filled.Favorite
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val scope = rememberCoroutineScope()
                var inputText by remember { mutableStateOf("") }

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
                        HandleButtonClick(inputText, scope)
                    }
                }
            }
        }
    }

    @Composable
    fun HandleButtonClick(inputText: String, scope: CoroutineScope) {
        ButtonValider(onClick = {
            if (inputText.isNotEmpty()) {
                scope.launch {
                    val coordinates = fetchCoordinates(inputText)
                    coordinates?.let { (latitude, longitude) ->
                        val intent = Intent(this@MainActivity, LocalMeteoActivity::class.java)
                        // Add latitude and longitude to the intent
                        intent.putExtra("latitude", latitude)
                        intent.putExtra("longitude", longitude)
                        startActivity(intent)
                    }
                }
            }
        })
    }

//-------------------------------------------


    suspend fun fetchCoordinates(cityName: String): Pair<Double, Double>? {
        val url = "https://nominatim.openstreetmap.org/search?q=$cityName&format=json"
        val request = Request.Builder()
            .url(url)
            .build()

        withContext(Dispatchers.IO) {
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
                    //TODO
                }

            } else {
                //TODO
            }

        }
        return null
    }


    //-------------------------------------------
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


//---------------------------------------------------------------------------------------//

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InputFieldPrerer(
        text: String,
        onValueChange: (String) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
            OutlinedTextField(
                value = text,
                onValueChange = onValueChange,
                label = { Text("Entrez vos villes preferées...") },
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
    fun ButtonValiderPreferer(onClick: () -> Unit) {
        Box(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier.padding(start = 8.dp)
                    .size(150.dp, 50.dp), // Modification de la taille du bouton
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xE93030)
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
}











