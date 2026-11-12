package com.cornellappdev.coursegrab.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityOptionsCompat
import com.cornellappdev.coursegrab.MainActivity
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme

class SplashScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        setContent {
            CourseGrabTheme {
                SplashScreen(onFinished = ::openApp)
            }
        }
    }

    private fun openApp() {
        if (isFinishing) return
        val fade = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(Intent(this, MainActivity::class.java), fade.toBundle())
        finish()
    }
}
