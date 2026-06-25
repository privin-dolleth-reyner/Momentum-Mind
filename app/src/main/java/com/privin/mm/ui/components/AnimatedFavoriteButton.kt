package com.privin.mm.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.privin.mm.ui.theme.HEART_RED
import com.privin.mm.ui.theme.MomentumMindTheme
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Favourite control with three coordinated micro-interactions on tap:
 *  - a springy scale "pop" (overshoot) on the heart,
 *  - a colour cross-fade from outline → filled red,
 *  - a one-shot particle burst that radiates out when a quote is favourited.
 *
 * State is hoisted: the displayed icon is driven entirely by [isFavorite], so it
 * stays correct across recomposition and re-entry. The animations are fired
 * imperatively on click so they feel instant regardless of how the upstream
 * state round-trips through the database.
 */
@Composable
fun AnimatedFavoriteButton(
    isFavorite: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
    inactiveTint: Color = LocalContentColor.current,
    onToggle: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val scale = remember { Animatable(1f) }
    val burst = remember { Animatable(0f) }
    var burstActive by remember { mutableStateOf(false) }

    val tint by animateColorAsState(
        targetValue = if (isFavorite) HEART_RED else inactiveTint,
        animationSpec = tween(durationMillis = 250),
        label = "heartTint",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (burstActive) {
            Canvas(modifier = Modifier.size(64.dp)) {
                val progress = burst.value
                val eased = FastOutSlowInEasing.transform(progress)
                val particleCount = 8
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.minDimension * 0.5f
                val dotRadius = (1f - progress) * (size.minDimension * 0.07f)
                val alpha = (1f - progress).coerceIn(0f, 1f)
                repeat(particleCount) { i ->
                    val angle = (2.0 * Math.PI * i / particleCount).toFloat()
                    val distance = maxRadius * eased
                    val pos = Offset(
                        x = center.x + cos(angle) * distance,
                        y = center.y + sin(angle) * distance,
                    )
                    drawCircle(
                        color = HEART_RED.copy(alpha = alpha),
                        radius = dotRadius.coerceAtLeast(0.5f),
                        center = pos,
                    )
                }
            }
        }

        IconButton(
            onClick = {
                val newValue = !isFavorite
                onToggle(newValue)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch {
                    scale.snapTo(0.7f)
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                    )
                }
                if (newValue) {
                    scope.launch {
                        burstActive = true
                        burst.snapTo(0f)
                        burst.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
                        )
                        burstActive = false
                    }
                }
            },
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.scale(scale.value),
            )
        }
    }
}

@Preview
@Composable
private fun AnimatedFavoriteButtonPreview() {
    MomentumMindTheme {
        var favorite by remember { mutableStateOf(true) }
        AnimatedFavoriteButton(
            isFavorite = favorite,
            contentDescription = "Favorite",
            onToggle = { favorite = it },
        )
    }
}
