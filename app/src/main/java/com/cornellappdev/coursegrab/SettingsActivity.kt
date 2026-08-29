package com.cornellappdev.coursegrab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cornellappdev.coursegrab.databinding.ActivitySettingsBinding
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect(::handleEffect)
            }
        }

        binding.emailAlertsSwitch.isChecked = viewModel.emailAlertsEnabled
        binding.mobileAlertsSwitch.isChecked = viewModel.mobileAlertsEnabled

        binding.emailAlertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEmailAlerts(isChecked)
        }

        binding.mobileAlertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (
                Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "Please enable notifications in settings to receive course updates",
                    Toast.LENGTH_LONG,
                ).show()
            } else {
                FirebaseMessaging.getInstance().isAutoInitEnabled = isChecked
                viewModel.setMobileAlerts(isChecked)
            }
        }

        binding.classRoster.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, "https://classes.cornell.edu/".toUri())
            startActivity(browserIntent)
        }

        binding.cornellAcademicCalendar.setOnClickListener {
            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    "https://registrar.cornell.edu/academic-calendar".toUri()
                )
            startActivity(browserIntent)
        }

        binding.signOut.setOnClickListener { viewModel.signOut() }

        binding.backBtn.setOnClickListener { finish() }

        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.mobileAlertsSwitch.isChecked = false
            binding.mobileAlertsSwitch.isEnabled = false
        }

    }

    private fun handleEffect(effect: SettingsEffect) {
        when (effect) {
            is SettingsEffect.Message ->
                Toast.makeText(this, effect.text, Toast.LENGTH_SHORT).show()

            SettingsEffect.SignedOut ->
                startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
