package com.cornellappdev.coursegrab.data

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject constructor(@ApplicationContext context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var sessionToken: String?
        get() = preferences.getString(SESSION_TOKEN, "")
        set(value) = preferences.edit { putString(SESSION_TOKEN, value) }

    var updateToken: String?
        get() = preferences.getString(UPDATE_TOKEN, "")
        set(value) = preferences.edit { putString(UPDATE_TOKEN, value) }

    var expiresAt: Long
        get() = preferences.getLong(EXPIRES_AT, 0L)
        set(value) = preferences.edit { putLong(EXPIRES_AT, value) }

    var emailAlertSetting: Boolean
        get() = preferences.getBoolean(EMAIL_ALERTS, true)
        set(value) = preferences.edit { putBoolean(EMAIL_ALERTS, value) }

    var mobileAlertSetting: Boolean
        get() = preferences.getBoolean(MOBILE_ALERTS, true)
        set(value) = preferences.edit { putBoolean(MOBILE_ALERTS, value) }

    fun clearAll() {
        preferences.edit(commit = true) { clear() }
    }

    companion object {
        private const val SESSION_TOKEN = "data.source.prefs.SESSION_TOKEN"
        private const val UPDATE_TOKEN = "data.source.prefs.UPDATE_TOKEN"
        private const val EXPIRES_AT = "data.source.prefs.EXPIRES_AT"
        private const val EMAIL_ALERTS = "data.source.prefs.EMAIL_ALERTS"
        private const val MOBILE_ALERTS = "data.source.prefs.MOBILE_ALERTS"
    }
}