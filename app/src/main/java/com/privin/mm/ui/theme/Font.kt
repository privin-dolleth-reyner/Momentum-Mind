@file:OptIn(ExperimentalTextApi::class)

package com.privin.mm.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.privin.mm.R

/**
 * Typography is bundled (not downloadable) so it renders offline, needs no Google
 * Play Services and never flashes a fallback typeface. Both files are variable
 * fonts, so each weight is materialised from the single `wght` axis via
 * [FontVariation]. On API < 26 variation settings are ignored and the font loads
 * at its default (regular) instance — an acceptable graceful degradation.
 */

private fun loraWeight(weight: FontWeight) = Font(
    resId = R.font.lora_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private fun interWeight(weight: FontWeight) = Font(
    resId = R.font.inter_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

/** Elegant serif used for quotes, screen titles and the author line. */
val DisplaySerif = FontFamily(
    loraWeight(FontWeight.Medium),
    loraWeight(FontWeight.SemiBold),
    loraWeight(FontWeight.Bold),
)

/** Clean sans used for body copy, labels and UI chrome. */
val BodySans = FontFamily(
    interWeight(FontWeight.Normal),
    interWeight(FontWeight.Medium),
    interWeight(FontWeight.SemiBold),
)
