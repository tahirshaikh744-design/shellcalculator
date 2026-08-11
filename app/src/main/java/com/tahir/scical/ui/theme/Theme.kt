package com.tahir.scical.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Color Scheme - Matching existing app theme
private val LightColorScheme = lightColorScheme(
    primary = Violet,
    onPrimary = White,
    secondary = TextAc,
    onSecondary = White,
    tertiary = BtnEqual,
    background = BgMain,
    onBackground = TextPrimary,
    surface = BgMain,
    onSurface = TextPrimary,
    surfaceVariant = BtnNumber,
    onSurfaceVariant = TextPrimary,
    outline = DividerColor
)

// Dark Color Scheme - Matching existing dark theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    tertiary = BtnEqualDark,
    background = BgMainDark,
    onBackground = TextPrimaryDark,
    surface = BgMainDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = BtnNumberDark,
    onSurfaceVariant = TextPrimaryDark,
    outline = DividerColorDark
)

@Composable
fun ScicalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to preserve brand colors
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