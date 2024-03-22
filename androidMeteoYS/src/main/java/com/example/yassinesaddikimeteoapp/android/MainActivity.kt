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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
                        ButtonValider(onClick = { /* TODO*/ })
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
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun InputField(
    text: String,
    onValueChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            label = { Text("Chercher une ville...") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f) // Pour étirer le champ de texte pour prendre tout l'espace disponible
        )
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search"
        )
    }
    ButtonValider(onClick = { /*TODO*/ })
}

@Composable
fun ButtonValider(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(start = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF55ABFA)
        ),
    ) {
        Text(
            text = "VALIDER",
            color = Color.White,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
    }
}











