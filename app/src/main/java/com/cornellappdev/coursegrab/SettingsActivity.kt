package com.cornellappdev.coursegrab

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess


class SettingsActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 10032

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        email_alerts_switch.isChecked = preferencesHelper.email_alert_setting
        mobile_alerts_switch.isChecked = preferencesHelper.mobile_alert_setting

        email_alerts_switch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.email_alert_setting = isChecked
        }

        mobile_alerts_switch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.mobile_alert_setting = isChecked
            FirebaseMessaging.getInstance().isAutoInitEnabled = isChecked

            setNotificationsStatus(isChecked)
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
                Intent(Intent.ACTION_VIEW, Uri.parse("https://registrar.cornell.edu/academic-calendar"))
            startActivity(browserIntent)
        }

        sign_out.setOnClickListener {
            preferencesHelper.clearAll();
            signOut()
        }

        back_btn.setOnClickListener { finish() }
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
                )
        }
    }

    private fun signOut() {
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val mStartActivity = Intent(this@SettingsActivity, LoginActivity::class.java)
            val mPendingIntent = PendingIntent.getActivity(
                this@SettingsActivity,
                RC_SIGN_IN,
                mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val mgr = this@SettingsActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
            exitProcess(0)
        }
    }
}
