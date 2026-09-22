package com.cornellappdev.coursegrab.data

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException

private const val TAG = "CredentialManager"

suspend fun CredentialManager.clearCredentialStateOrLog() {
    try {
        clearCredentialState(ClearCredentialStateRequest())
    } catch (e: ClearCredentialException) {
        Log.w(TAG, "Failed to clear credential state", e)
    }
}
