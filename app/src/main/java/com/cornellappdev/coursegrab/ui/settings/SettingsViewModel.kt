package com.cornellappdev.coursegrab.ui.settings

import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coursegrab.data.PreferencesHelper
import com.cornellappdev.coursegrab.data.clearCredentialStateOrLog
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEffect {
    data class Message(val text: String) : SettingsEffect
    data object SignedOut : SettingsEffect
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val credentialManager: CredentialManager,
    private val repository: CourseGrabRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val emailAlertsEnabled: Boolean get() = preferencesHelper.emailAlertSetting
    val mobileAlertsEnabled: Boolean get() = preferencesHelper.mobileAlertSetting

    fun setEmailAlerts(enabled: Boolean) {
        preferencesHelper.emailAlertSetting = enabled
    }

    fun setMobileAlerts(enabled: Boolean) {
        preferencesHelper.mobileAlertSetting = enabled
        viewModelScope.launch {
            repository.setNotifications(enabled)
                .onSuccess {
                    _effects.send(
                        SettingsEffect.Message(
                            "Notifications ${if (enabled) "enabled." else "disabled."}"
                        )
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to update notification setting", error)
                    _effects.send(
                        SettingsEffect.Message("Couldn't update notification settings.")
                    )
                }
        }
    }

    fun signOut() {
        preferencesHelper.clearAll()
        viewModelScope.launch {
            credentialManager.clearCredentialStateOrLog()
            _effects.send(SettingsEffect.SignedOut)
        }
    }

    private companion object {
        const val TAG = "SettingsViewModel"
    }
}
