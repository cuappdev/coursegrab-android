package com.cornellappdev.coursegrab

import android.content.Context
import android.preference.PreferenceManager

class PreferencesHelper(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var sessionToken = preferences.getString(SESSION_TOKEN, "")
        set(value) {
            preferences.edit().putString(SESSION_TOKEN, value).apply()
            field = preferences.getString(SESSION_TOKEN, "")
        }

    var updateToken = preferences.getString(UPDATE_TOKEN, "")
        set(value) {
            preferences.edit().putString(UPDATE_TOKEN, value).apply()
            field = preferences.getString(UPDATE_TOKEN, "")
        }

    var expiresAt = preferences.getLong(EXPIRES_AT, 0L)
        set(value) {
            preferences.edit().putLong(EXPIRES_AT, value).apply()
            field = preferences.getLong(EXPIRES_AT, 0L)
        }

    companion object {
        private const val SESSION_TOKEN = "data.source.prefs.SESSION_TOKEN"
        private const val UPDATE_TOKEN = "data.source.prefs.UPDATE_TOKEN"
        private const val EXPIRES_AT = "data.source.prefs.EXPIRES_AT"
    }
}