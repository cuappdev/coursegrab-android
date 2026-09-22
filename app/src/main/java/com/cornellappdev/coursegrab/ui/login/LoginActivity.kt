package com.cornellappdev.coursegrab.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.cornellappdev.coursegrab.ui.main.MainActivity
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hand Credential Manager the current Activity. On a configuration change this runs
        // again with the new instance, so a sign-in already in flight keeps its UI host.
        viewModel.credentialContext.baseContext = this

        setContent {
            CourseGrabTheme {
                LoginRoute(
                    onNavigateToMain = {
                        startActivity(Intent(this, MainActivity::class.java))
                    },
                    // The Activity already resolved this ViewModel to wire up
                    // credentialContext; hiltViewModel() would resolve the same instance,
                    // but passing it keeps the two references provably identical.
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onDestroy() {
        // Paired with onCreate: the ViewModel outlives this Activity, so hand the wrapper
        // back to the application context rather than leaving a destroyed Activity in it.
        viewModel.credentialContext.baseContext = applicationContext
        super.onDestroy()
    }
}
