package com.hastakala.shop.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = Cream,
    primaryContainer = TerracottaSoft,
    onPrimaryContainer = Terracotta,
    secondary = Indigo,
    onSecondary = Cream,
    secondaryContainer = InfoBlue,
    onSecondaryContainer = Indigo,
    background = Cream,
    onBackground = InkDark,
    surface = Cream,
    onSurface = InkDark,
    surfaceVariant = CreamSoft,
    onSurfaceVariant = InkMuted,
    error = DangerRed,
    onError = Cream,
    errorContainer = DangerSoft,
    onErrorContainer = DangerRed,
    outlineVariant = Color(0xFFEDE5DA)
)

private val DarkColors = darkColorScheme(
    primary = TerracottaSoft,
    onPrimary = InkDark,
    primaryContainer = Terracotta.copy(alpha = 0.3f),
    onPrimaryContainer = TerracottaSoft,
    secondary = Sand,
    onSecondary = InkDark,
    secondaryContainer = Charcoal,
    onSecondaryContainer = CreamSoft,
    background = InkDark,
    onBackground = Cream,
    surface = Color(0xFF242424),
    onSurface = Cream,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Sand,
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    outlineVariant = Color(0xFF3D3D3D)
)

private val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Black, fontSize = 36.sp, lineHeight = 42.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp),
)

@Composable
fun HastaKalaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, content = content)
}
