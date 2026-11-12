package com.cornellappdev.coursegrab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.cornellappdev.coursegrab.ui.navigation.CourseGrabNavHost
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_SPLASH_MS = 2000L

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var contentReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !contentReady }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(MAX_SPLASH_MS)
            contentReady = true
        }

        setContent {
            CourseGrabTheme {
                CourseGrabNavHost(onReady = { contentReady = true })
            }
        }
    }
}
