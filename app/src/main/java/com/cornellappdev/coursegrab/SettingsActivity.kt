package com.cornellappdev.coursegrab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.lifecycleScope
import com.cornellappdev.coursegrab.databinding.ActivitySettingsBinding
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.setNotification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(this)
    }

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailAlertsSwitch.isChecked = preferencesHelper.emailAlertSetting
        binding.mobileAlertsSwitch.isChecked = preferencesHelper.mobileAlertSetting

        binding.emailAlertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.emailAlertSetting = isChecked
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
                preferencesHelper.mobileAlertSetting = isChecked
                FirebaseMessaging.getInstance().isAutoInitEnabled = isChecked

                setNotificationsStatus(isChecked)
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

        binding.signOut.setOnClickListener {
            preferencesHelper.clearAll()
            signOut()
        }

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

    private fun setNotificationsStatus(enabled: Boolean) {
        val setNotifs = Endpoint.setNotification(
            preferencesHelper.sessionToken.toString(),
            if (enabled) "ANDROID" else "NONE"
        )

        lifecycleScope.launch {
            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
            val response = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<Course>>(
                    setNotifs.okHttpRequest(),
                    typeToken
                )
            }

            if (response!!.success)
                Toast.makeText(
                    this@SettingsActivity,
                    "Notifications ${if (enabled) "enabled." else "disabled."}",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    private fun signOut() {
        lifecycleScope.launch {
            // Clears the Credential Manager provider state so the account picker
            // reappears on the next sign-in attempt.
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: ClearCredentialException) {
                Log.w(TAG, "Failed to clear credential state", e)
            }

            startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
        }
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }
}
