package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = ForestGreenDark,
    onPrimary = Color.Black,
    primaryContainer = ForestGreenContainerDark,
    onPrimaryContainer = MintLight,
    secondary = EmeraldDark,
    onSecondary = Color.Black,
    secondaryContainer = EmeraldContainerDark,
    onSecondaryContainer = MintLight,
    tertiary = CoralAccent,
    background = SurfaceDarkCharcoal,
    surface = SurfaceContainerDark,
    onBackground = OnSurfaceLightIvory,
    onSurface = OnSurfaceLightIvory,
    outline = OutlineMuted
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreenLight,
    onPrimary = Color.White,
    primaryContainer = ForestGreenContainerLight,
    onPrimaryContainer = OnForestGreenContainerLight,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainerLight,
    onSecondaryContainer = ForestGreenPrimary,
    tertiary = CoralAccent,
    background = SurfaceWarmIvory,
    surface = SurfaceContainerWarm,
    onBackground = OnSurfaceCharcoal,
    onSurface = OnSurfaceCharcoal,
    outline = OutlineMuted
  )

@Composable
fun SharePlateTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep SharePlate brand cohesive
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) = SharePlateTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)

