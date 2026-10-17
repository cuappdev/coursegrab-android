package com.cornellappdev.coursegrab.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cornellappdev.coursegrab.ui.main.MainActivity
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CourseGrabTheme {
                LoginScreen(
                    onNavigateToMain = {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                )
            }
        }
    }
}
