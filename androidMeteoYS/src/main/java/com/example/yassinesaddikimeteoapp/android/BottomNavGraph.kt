package com.example.yassinesaddikimeteoapp.android


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yassinesaddikimeteoapp.android.screens.HomeScreen
import com.example.yassinesaddikimeteoapp.android.screens.LocalScreen
import com.example.yassinesaddikimeteoapp.android.screens.ShareScreen
import com.example.yassinesaddikimeteoapp.android.screens.FavoriteScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Favorite.route) {
            FavoriteScreen()
        }
        composable(route = BottomBarScreen.Local.route) {
            LocalScreen()
        }
        composable(route = BottomBarScreen.Share.route) {
            ShareScreen()
        }
    }
}