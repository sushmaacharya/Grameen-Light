package com.grameenlight.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DayColors = lightColorScheme(
    primary = PrimaryVillageGreen,
    secondary = AssignedBlue,
    tertiary = DayBurningAmber,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    error = FusedRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF20251F),
    onSurface = Color(0xFF20251F),
    onSurfaceVariant = LightOnSurfaceMuted
)

private val NightColors = darkColorScheme(
    primary = Color(0xFF4FA36A),
    secondary = AssignedBlue,
    tertiary = WorkingGreen,
    background = NightBackground,
    surface = NightSurface,
    surfaceVariant = NightSurfaceVariant,
    error = FusedRed,
    onPrimary = Color(0xFF07120A),
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE7EEE8),
    onSurface = Color(0xFFE7EEE8),
    onSurfaceVariant = NightOnSurfaceMuted
)

@Composable
fun GrameenLightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) NightColors else DayColors,
        typography = MaterialTheme.typography,
        content = content
    )
}
