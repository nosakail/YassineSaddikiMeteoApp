package com.example.yassinesaddikimeteoapp.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline



import androidx.compose.ui.graphics.vector.ImageVector



sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home: BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Favorite: BottomBarScreen(
        route = "favorite",
        title = "Favorite",
        icon = Icons.Default.Favorite
    )
    object Local: BottomBarScreen(
        route = "local",
        title = "Local",
        icon = Icons.Default.LocationOn
    )
    object Share: BottomBarScreen(
        route = "share",
        title = "Share",
        icon = Icons.Default.MailOutline
    )
}
