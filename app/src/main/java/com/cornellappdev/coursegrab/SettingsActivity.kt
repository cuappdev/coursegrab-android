package com.cornellappdev.coursegrab

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_settings.*
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
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_out.setOnClickListener {
            preferencesHelper.clearAll();
            signOut()
        }

        back_btn.setOnClickListener { finish() }
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
