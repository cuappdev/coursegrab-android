package com.cornellappdev.coursegrab.ui.components

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat

@Composable
fun StatusBarIcons(onDarkBackground: Boolean) {
    val window = LocalActivity.current?.window ?: return
    DisposableEffect(window, onDarkBackground) {
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !onDarkBackground
        onDispose {}
    }
}
