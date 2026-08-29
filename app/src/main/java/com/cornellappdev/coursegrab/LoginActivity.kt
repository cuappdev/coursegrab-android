package com.cornellappdev.coursegrab

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cornellappdev.coursegrab.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hand Credential Manager the current Activity. On a configuration change this runs
        // again with the new instance, so a sign-in already in flight keeps its UI host.
        viewModel.credentialContext.baseContext = this

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect(::handleEffect)
            }
        }

        binding.signInButton.setOnClickListener { viewModel.signIn() }
    }

    override fun onDestroy() {
        // Paired with onCreate: the ViewModel outlives this Activity, so hand the wrapper
        // back to the application context rather than leaving a destroyed Activity in it.
        viewModel.credentialContext.baseContext = applicationContext
        super.onDestroy()
    }

    private fun handleEffect(effect: LoginEffect) {
        when (effect) {
            is LoginEffect.Error ->
                Snackbar.make(binding.loginRootView, effect.message, Snackbar.LENGTH_LONG).show()

            LoginEffect.NavigateToMain ->
                startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
