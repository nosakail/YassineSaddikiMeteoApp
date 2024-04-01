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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
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
                        ButtonValider(onClick = {
                            if (inputText.isNotEmpty()) {
                                val intent = Intent(this@MainActivity, LocalMeteoActivity::class.java)
                                intent.putExtra("cityName", inputText)
                                startActivity(intent)
                            }
                        })
                    }
                }
            }
        }
    }
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
            modifier = Modifier.padding(start = 8.dp).size(150.dp, 50.dp), // Modification de la taille du bouton
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












