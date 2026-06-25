package com.privin.mm.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

/** Screen-background gradient stops for the current theme. */
@Composable
fun appGradientColors(dark: Boolean = isSystemInDarkTheme()): List<androidx.compose.ui.graphics.Color> =
    if (dark) listOf(GRADIENT_DARK_1, GRADIENT_DARK_2, GRADIENT_DARK_3)
    else listOf(GRADIENT_LIGHT_1, GRADIENT_LIGHT_2, GRADIENT_LIGHT_3)

/** Diagonal gradient brush for the elevated quote card. */
@Composable
fun quoteCardBrush(dark: Boolean = isSystemInDarkTheme()): Brush {
    val colors = if (dark) listOf(CARD_DARK_1, CARD_DARK_2) else listOf(CARD_LIGHT_1, CARD_LIGHT_2)
    return Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset.Infinite,
    )
}

/**
 * A living background: a multi-stop linear gradient whose start/end offsets drift
 * slowly (≈12s loop) so the colour field gently breathes. Amplitude is kept small
 * to stay calm and battery-friendly.
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = appGradientColors(dark)
    val transition = rememberInfiniteTransition(label = "gradient")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradientProgress",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val w = size.width
                val h = size.height
                val start = Offset(x = w * (0.15f + 0.30f * progress), y = h * (0.10f * progress))
                val end = Offset(x = w * (0.85f - 0.20f * progress), y = h * (1f - 0.10f * progress))
                drawRect(
                    brush = Brush.linearGradient(colors = colors, start = start, end = end),
                )
            },
    ) {
        content()
    }
}
