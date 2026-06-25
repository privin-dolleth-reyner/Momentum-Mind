package com.privin.mm

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

private val tabOrder = listOf(Screen.Home.route, Screen.Favorites.route)

/** +1 when moving to a tab on the right, -1 when moving left. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideDirection(): Int {
    val from = tabOrder.indexOf(initialState.destination.route)
    val to = tabOrder.indexOf(targetState.destination.route)
    return if (to >= from) 1 else -1
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier,
    showSnackbar: (String) -> Unit,
    showUndoSnackbar: (message: String, actionLabel: String, onUndo: () -> Unit) -> Unit,
) {
    val anim = tween<IntOffset>(durationMillis = 300)
    val fade = tween<Float>(durationMillis = 300)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            val dir = slideDirection()
            slideInHorizontally(anim) { fullWidth -> dir * fullWidth } + fadeIn(fade)
        },
        exitTransition = {
            val dir = slideDirection()
            slideOutHorizontally(anim) { fullWidth -> -dir * fullWidth } + fadeOut(fade)
        },
        popEnterTransition = {
            val dir = slideDirection()
            slideInHorizontally(anim) { fullWidth -> dir * fullWidth } + fadeIn(fade)
        },
        popExitTransition = {
            val dir = slideDirection()
            slideOutHorizontally(anim) { fullWidth -> -dir * fullWidth } + fadeOut(fade)
        },
    ) {
        composable(Screen.Home.route) { HomeScreen(showSnackBar = showSnackbar) }
        composable(Screen.Favorites.route) { FavoritesScreen(showUndoSnackbar = showUndoSnackbar) }
    }
}

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    data object Home : Screen("home", Icons.Filled.Home, "Home")
    data object Favorites : Screen("favorites", Icons.Filled.Favorite, "Favorites")
}
