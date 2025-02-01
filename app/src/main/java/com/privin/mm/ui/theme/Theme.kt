package com.privin.mm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BLUE10,
    onPrimary = BLUE90,
    primaryContainer = BLUE40,
    onPrimaryContainer = BLUE90,
    inversePrimary = BLUE80,
    secondary = LIGHT_BLUE_10,
    onSecondary = LIGHT_BLUE_90,
    secondaryContainer = LIGHT_BLUE_40,
    onSecondaryContainer = LIGHT_BLUE_90,
    tertiary = BROWN10,
    onTertiary = BROWN90,
    tertiaryContainer = BROWN40,
    onTertiaryContainer = BROWN90,
    error = RED40,
    onError = RED90,
    errorContainer = RED40,
    background = WHITE_10,
    onBackground = WHITE_90,
    surface = WHITE_10,
    surfaceContainer = WHITE_40,
    onSurface = WHITE_90,
    inverseSurface = WHITE_90,
    inverseOnSurface = WHITE_10,
)

private val LightColorScheme = lightColorScheme(
    primary = BLUE40,
    onPrimary = WHITE,
    primaryContainer = BLUE10,
    onPrimaryContainer = WHITE,
    inversePrimary = BLUE80,
    secondary = LIGHT_BLUE_40,
    onSecondary = WHITE,
    secondaryContainer = LIGHT_BLUE_80,
    onSecondaryContainer = WHITE,
    tertiary = BROWN40,
    onTertiary = WHITE,
    tertiaryContainer = BROWN80,
    onTertiaryContainer = WHITE,
    error = RED40,
    onError = WHITE,
    errorContainer = RED80,
    background = WHITE_90,
    onBackground = BLACK_TEXT,
    surface = WHITE_80,
    surfaceContainer = WHITE_80,
    onSurface = WHITE_10,
    inverseOnSurface = WHITE_90,
    inverseSurface = WHITE_10,
)

@Composable
fun MomentumMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}