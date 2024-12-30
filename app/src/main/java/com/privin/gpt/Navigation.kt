package com.privin.gpt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier,
    showSnackbar: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) { HomeScreen(showSnackbar = showSnackbar) }
        composable(Screen.Favorites.route) { FavoritesScreen() }
    }
}

sealed class Screen(val route: String, val icon: ImageVector, val label: String){
    data object Home: Screen("home", Icons.Filled.Home, "Home")
    data object Favorites: Screen("favorites", Icons.Filled.Favorite, "Favorites")
}