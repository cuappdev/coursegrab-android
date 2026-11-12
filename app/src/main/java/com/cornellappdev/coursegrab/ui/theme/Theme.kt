package com.cornellappdev.coursegrab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CourseGrabColorScheme = lightColorScheme(
    primary = CourseGrabColors.Black,
    onPrimary = CourseGrabColors.White,
    background = CourseGrabColors.White,
    onBackground = CourseGrabColors.Black,
    surface = CourseGrabColors.White,
    onSurface = CourseGrabColors.Black,
    onSurfaceVariant = CourseGrabColors.DarkGray,
    error = CourseGrabColors.Red,
    onError = CourseGrabColors.White,
    outline = CourseGrabColors.Gray
)

@Composable
fun CourseGrabTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CourseGrabColorScheme, content = content)
}
