
package com.example.tripplethreat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Light mode
private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    secondary = Teal,
    onSecondary = Color.White,
    tertiary = Gold,
    onTertiary = DarkText,
    background = AppBackground,
    onBackground = DarkText,
    surface = Color.White,
    onSurface = DarkText
)

// Dark mode
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA9C8F2),
    onPrimary = Color(0xFF102942),
    secondary = Color(0xFF75D5DC),
    onSecondary = Color(0xFF00363D),
    tertiary = Color(0xFFF4CF85),
    onTertiary = Color(0xFF463300),
    background = Color(0xFF101C2C),
    onBackground = Color(0xFFEAF0F8),
    surface = Color(0xFF1A2A3E),
    onSurface = Color(0xFFEAF0F8)
)

// App typography
private val EarlyEnlightenmentTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

@Composable
fun TrippleThreatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EarlyEnlightenmentTypography,
        content = content
    )
}
