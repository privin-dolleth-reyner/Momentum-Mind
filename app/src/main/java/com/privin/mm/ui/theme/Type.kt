package com.privin.mm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Editorial type system: the serif [DisplaySerif] (Lora) carries the quotes,
 * titles and author line, while [BodySans] (Inter) handles UI chrome, body copy
 * and labels. Starts from the Material 3 baseline and overrides only the styles
 * the app actually uses.
 */
private val base = Typography()

val Typography = base.copy(
    // Quote text — large, warm serif with relaxed line height.
    displayLarge = base.displayLarge.copy(
        fontFamily = DisplaySerif,
        fontWeight = FontWeight.Medium,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = base.displayMedium.copy(fontFamily = DisplaySerif, fontWeight = FontWeight.Medium),
    displaySmall = base.displaySmall.copy(fontFamily = DisplaySerif, fontWeight = FontWeight.Medium),

    // Screen titles.
    headlineLarge = base.headlineLarge.copy(
        fontFamily = DisplaySerif,
        fontWeight = FontWeight.SemiBold,
    ),
    headlineMedium = base.headlineMedium.copy(
        fontFamily = DisplaySerif,
        fontWeight = FontWeight.Medium,
    ),
    headlineSmall = base.headlineSmall.copy(fontFamily = DisplaySerif, fontWeight = FontWeight.Medium),

    // Quote snippet in the favourites list.
    titleLarge = base.titleLarge.copy(
        fontFamily = DisplaySerif,
        fontWeight = FontWeight.Medium,
        lineHeight = 30.sp,
    ),
    titleMedium = base.titleMedium.copy(fontFamily = BodySans, fontWeight = FontWeight.Medium),
    titleSmall = base.titleSmall.copy(fontFamily = BodySans, fontWeight = FontWeight.Medium),

    // UI / body / labels — clean sans.
    bodyLarge = base.bodyLarge.copy(fontFamily = BodySans),
    bodyMedium = base.bodyMedium.copy(fontFamily = BodySans),
    bodySmall = base.bodySmall.copy(fontFamily = BodySans),
    labelLarge = base.labelLarge.copy(fontFamily = BodySans, fontWeight = FontWeight.Medium),
    labelMedium = base.labelMedium.copy(fontFamily = BodySans, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = BodySans, fontWeight = FontWeight.Medium),
)

/** A few extra non-Material styles for tuned moments. */
val QuoteAuthorStyle: TextStyle = TextStyle(
    fontFamily = BodySans,
    fontWeight = FontWeight.Medium,
    letterSpacing = 1.sp,
)
