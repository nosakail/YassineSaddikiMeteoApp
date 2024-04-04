package com.example.yassinesaddikimeteoapp.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BlueNB,
)
@Composable
fun ComposeMVVMWeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}