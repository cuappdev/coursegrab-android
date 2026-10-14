package com.cornellappdev.coursegrab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CourseGrabColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealDark,
    secondary = Pink,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = MutedText,
    error = RemovalRed,
    onError = Color.White,
    outline = LightText
)

@Composable
fun CourseGrabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CourseGrabColorScheme,
        typography = CourseGrabTypography,
        content = content
    )
}
