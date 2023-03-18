package com.loukwn.gifsoundit.presentation.common.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val GsBurgundy = Color(0xFF491505)
val GsBurgundyDark = Color(0xFF2B0C03)
val GsOrange = Color(0xFFFF8800)
val GsGreen = Color(0xFF669900)
val GsBlue = Color(0xFF0099CC)
val GsBlack = Color(0xFF000000)
val GsDarkestGray = Color(0xFF212121)
val GsDarkGray = Color(0xFF303030)
val GsLightGray = Color(0xFFAAAAAA)
val GsLightestGray = Color(0xFFE6E6E5)
val GsWhite = Color(0xFFFFFFFF)


data class GsColors(
    val material: Colors,
    val onSurfaceAdditional: Color,
    val postScoreTextColor: Color,
    val postScoreBorderColor: Color,
    val postScoreBgColor: Color,
) {
    val primary: Color get() = material.primary
    val primaryVariant: Color get() = material.primaryVariant
    val background: Color get() = material.background
    val surface: Color get() = material.surface
    val onSurface: Color get() = material.onSurface
}

// Things that are not different across theme changes
val GsColors.featureHot: Color get() = GsOrange
val GsColors.featureNew: Color get() = GsGreen
val GsColors.featureTop: Color get() = GsBlue

internal val LocalColors = staticCompositionLocalOf { LightColorPalette }

internal val DarkColorPalette = GsColors(
    material = darkColors(
        primary = GsBurgundy,
        primaryVariant = GsBurgundyDark,
        background = GsDarkestGray,
        onPrimary = GsWhite,
        surface = GsDarkGray,
        onSurface = GsWhite,
    ),
    onSurfaceAdditional = GsLightGray,
    postScoreBgColor = GsDarkestGray,
    postScoreBorderColor = GsDarkestGray,
    postScoreTextColor = GsWhite,
)

internal val LightColorPalette = GsColors(
    material = lightColors(
        primary = GsBurgundy,
        primaryVariant = GsBurgundyDark,
        background = GsLightestGray,
        onPrimary = GsWhite,
        surface = GsWhite,
        onSurface = GsBlack,
    ),
    onSurfaceAdditional = GsDarkGray,
    postScoreBgColor = GsBurgundy,
    postScoreBorderColor = GsBurgundyDark,
    postScoreTextColor = GsWhite,
)

val gsColors: GsColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current
