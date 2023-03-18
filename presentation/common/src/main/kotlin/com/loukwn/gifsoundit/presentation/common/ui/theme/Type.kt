package com.loukwn.gifsoundit.presentation.common.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.loukwn.gifsoundit.presentation.common.R

private val overlockFontFamily = FontFamily(
    Font(R.font.overlock_regular),
    Font(R.font.overlock_black, weight = FontWeight.Black)
)

private val hindsiliguriFontFamily = FontFamily(
    Font(R.font.hindsiliguri_regular),
    Font(R.font.hindsiliguri_medium, weight = FontWeight.Medium),
    Font(R.font.hindsiliguri_bold, weight = FontWeight.Bold),
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = overlockFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp
    ),
    h2 = TextStyle(
        fontFamily = hindsiliguriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)
