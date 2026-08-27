package com.cornellappdev.coursegrab

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

class PreferencesHelper(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var sessionToken = preferences.getString(SESSION_TOKEN, "")
        set(value) {
            preferences.edit { putString(SESSION_TOKEN, value) }
            field = preferences.getString(SESSION_TOKEN, "")
        }

    var updateToken = preferences.getString(UPDATE_TOKEN, "")
        set(value) {
            preferences.edit { putString(UPDATE_TOKEN, value) }
            field = preferences.getString(UPDATE_TOKEN, "")
        }

    var expiresAt = preferences.getLong(EXPIRES_AT, 0L)
        set(value) {
            preferences.edit { putLong(EXPIRES_AT, value) }
            field = preferences.getLong(EXPIRES_AT, 0L)
        }

    var emailAlertSetting = preferences.getBoolean(EMAIL_ALERTS, true)
        set(value) {
            preferences.edit { putBoolean(EMAIL_ALERTS, value) }
            field = preferences.getBoolean(EMAIL_ALERTS, true)
        }

    var mobileAlertSetting = preferences.getBoolean(MOBILE_ALERTS, true)
        set(value) {
            preferences.edit { putBoolean(MOBILE_ALERTS, value) }
            field = preferences.getBoolean(MOBILE_ALERTS, true)
        }

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