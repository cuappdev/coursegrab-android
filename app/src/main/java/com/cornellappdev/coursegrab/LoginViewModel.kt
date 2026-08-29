package com.cornellappdev.coursegrab

import android.annotation.SuppressLint
import android.content.Context
import android.content.MutableContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.coursegrab.models.UserSession
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginEffect {
    data class Error(val message: String) : LoginEffect
    data object NavigateToMain : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: CourseGrabRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    /**
     * Credential Manager needs an activity context to launch its system UI, but the request
     * outlives the Activity across a configuration change. The Activity swaps its own
     * reference in on every [android.app.Activity.onCreate] and swaps it back out in
     * onDestroy, so a destroyed Activity is never reachable from here.
     *
     * Lint can't see that pairing, only that a ViewModel holds a Context.
     */
    @SuppressLint("StaticFieldLeak")
    val credentialContext = MutableContextWrapper(context)

    private val credentialManager by lazy { CredentialManager.create(context) }

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    /** Guards against a second sign-in being started while one is already in flight. */
    private var signingIn = false

    init {
        resumeExistingSession()
    }

    override fun onCleared() {
        // Don't leave the destroyed Activity reachable through the wrapper.
        credentialContext.baseContext = context
    }

    private fun resumeExistingSession() {
        if (preferencesHelper.expiresAt > System.currentTimeMillis() / 1000L) {
            viewModelScope.launch { _effects.send(LoginEffect.NavigateToMain) }
            return
        }

        val updateToken = preferencesHelper.updateToken
        if (updateToken.isNullOrBlank()) return

        viewModelScope.launch {
            repository.updateSession(updateToken)
                .onSuccess { verifySession(it) }
                .onFailure { Log.d(TAG, "Could not resume previous session", it) }
        }
    }

    fun signIn() {
        if (signingIn) return
        signingIn = true

        viewModelScope.launch {
            try {
                // Step 1: accounts already authorized for this app. Returning users get a
                // streamlined sheet, or no prompt at all when there is exactly one match.
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .setAutoSelectEnabled(true)
                    .build()

                when (val authorized = requestCredential(googleIdOption)) {
                    is CredentialResult.Success -> {
                        handleSignIn(authorized.response)
                        return@launch
                    }

                    CredentialResult.NoneAvailable -> Unit
                    CredentialResult.Cancelled, CredentialResult.Failed -> return@launch
                }

                // Step 2: first-time (or de-authorized) users get the full account picker.
                val signInWithGoogleOption = GetSignInWithGoogleOption
                    .Builder(context.getString(R.string.default_web_client_id))
                    .build()

                when (val result = requestCredential(signInWithGoogleOption)) {
                    is CredentialResult.Success -> handleSignIn(result.response)
                    CredentialResult.NoneAvailable ->
                        emitError("No Google account found. Add one in system settings.")

                    CredentialResult.Cancelled, CredentialResult.Failed -> Unit
                }
            } finally {
                signingIn = false
            }
        }
    }

    private suspend fun requestCredential(option: CredentialOption): CredentialResult {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        return try {
            CredentialResult.Success(
                credentialManager.getCredential(credentialContext, request)
            )
        } catch (e: NoCredentialException) {
            Log.d(TAG, "No matching credential for ${option::class.simpleName}", e)
            CredentialResult.NoneAvailable
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Sign-in cancelled by user", e)
            CredentialResult.Cancelled
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager sign-in failed", e)
            emitError(SIGN_IN_FAILED)
            CredentialResult.Failed
        }
    }

    private sealed interface CredentialResult {
        data class Success(val response: GetCredentialResponse) : CredentialResult
        data object NoneAvailable : CredentialResult
        data object Cancelled : CredentialResult
        data object Failed : CredentialResult
    }

    private suspend fun handleSignIn(response: GetCredentialResponse) {
        val credential = response.credential
        when (credential) {
            is CustomCredential -> {
                // GetSignInWithGoogleOption returns the SIWG credential type, while the
                // bottom-sheet GetGoogleIdOption flow returns the plain one. Accept either.
                if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL &&
                    credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL
                ) {
                    Log.e(TAG, "Unexpected credential type: ${credential.type}")
                    emitError(SIGN_IN_FAILED)
                    return
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential: ${credential::class}")
                emitError(SIGN_IN_FAILED)
                return
            }
        }

        val googleCredential = try {
            GoogleIdTokenCredential.createFrom(credential.data)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Failed to parse Google ID token", e)
            emitError(SIGN_IN_FAILED)
            return
        }

        if (!isAllowedAccount(googleCredential.id)) {
            emitError("Please use a @cornell.edu account")
            clearCredentialState()
            return
        }

        repository.initializeSession(googleCredential.idToken, null)
            .onSuccess { verifySession(it) }
            .onFailure { failure ->
                Log.e(TAG, "Failed to initialize session", failure)
                emitError(SIGN_IN_FAILED)
            }
    }

    private fun isAllowedAccount(email: String): Boolean =
        email.endsWith("@cornell.edu") ||
                email == "appstoreappdev@gmail.com" ||
                email == "coursegrab.droid@gmail.com"

    private fun clearCredentialState() {
        viewModelScope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: ClearCredentialException) {
                Log.w(TAG, "Failed to clear credential state", e)
            }
        }
    }

    private suspend fun verifySession(userSession: UserSession) {
        if (userSession.session_expiration.isNullOrBlank() ||
            userSession.session_token.isNullOrBlank() ||
            userSession.update_token.isNullOrBlank()
        ) return

        preferencesHelper.sessionToken = userSession.session_token
        preferencesHelper.updateToken = userSession.update_token
        preferencesHelper.expiresAt = userSession.session_expiration.toLong()

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            sendRegistrationToServer(token)
        }

        setNotificationsStatus(preferencesHelper.mobileAlertSetting)

        _effects.send(LoginEffect.NavigateToMain)
    }

    private fun sendRegistrationToServer(token: String) {
        viewModelScope.launch {
            repository.sendDeviceToken(token)
                .onSuccess { Log.d(TAG, "sendRegistrationTokenToServer($token)") }
                .onFailure { Log.w(TAG, "Failed to register device token", it) }
        }
    }

    private fun setNotificationsStatus(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotifications(enabled)
                .onFailure { Log.w(TAG, "Failed to update notifications", it) }
        }
    }

    private suspend fun emitError(message: String) {
        _effects.send(LoginEffect.Error(message))
    }

    private companion object {
        const val TAG = "LoginViewModel"
        const val SIGN_IN_FAILED = "Sign-in failed. Please try again."
    }
}
