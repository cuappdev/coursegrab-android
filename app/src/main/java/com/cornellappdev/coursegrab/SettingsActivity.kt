package com.cornellappdev.coursegrab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.setNotification
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_settings.back_btn
import kotlinx.android.synthetic.main.activity_settings.class_roster
import kotlinx.android.synthetic.main.activity_settings.cornell_academic_calendar
import kotlinx.android.synthetic.main.activity_settings.email_alerts_switch
import kotlinx.android.synthetic.main.activity_settings.mobile_alerts_switch
import kotlinx.android.synthetic.main.activity_settings.sign_out
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        email_alerts_switch.isChecked = preferencesHelper.emailAlertSetting
        mobile_alerts_switch.isChecked = preferencesHelper.mobileAlertSetting

        email_alerts_switch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.emailAlertSetting = isChecked
        }

        mobile_alerts_switch.setOnCheckedChangeListener { _, isChecked ->
            if (
                Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "Please enable notification permission in settings!",
                    Toast.LENGTH_LONG,
                ).show()
            } else {
                preferencesHelper.mobileAlertSetting = isChecked
                FirebaseMessaging.getInstance().isAutoInitEnabled = isChecked

                setNotificationsStatus(isChecked)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        class_roster.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://classes.cornell.edu/"))
            startActivity(browserIntent)
        }

        cornell_academic_calendar.setOnClickListener {
            val browserIntent =
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://registrar.cornell.edu/academic-calendar")
                )
            startActivity(browserIntent)
        }

        sign_out.setOnClickListener {
            preferencesHelper.clearAll()
            signOut()
        }

        back_btn.setOnClickListener { finish() }

        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mobile_alerts_switch.isChecked = false
            mobile_alerts_switch.isEnabled = false
        }

    }

    private fun setNotificationsStatus(enabled: Boolean) {
        val setNotifs = Endpoint.setNotification(
            preferencesHelper.sessionToken.toString(),
            if (enabled) "ANDROID" else "NONE"
        )

        CoroutineScope(Dispatchers.Main).launch {
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
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val mStartActivity = Intent(this@SettingsActivity, LoginActivity::class.java)
            startActivity(mStartActivity)
        }
    }
}
