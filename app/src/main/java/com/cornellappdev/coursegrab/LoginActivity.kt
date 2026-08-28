package com.cornellappdev.coursegrab

import android.content.Intent
import android.content.MutableContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import androidx.lifecycle.lifecycleScope
import com.cornellappdev.coursegrab.databinding.ActivityLoginBinding
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.Course
import com.cornellappdev.coursegrab.models.UserSession
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.deviceToken
import com.cornellappdev.coursegrab.networking.initializeSession
import com.cornellappdev.coursegrab.networking.setNotification
import com.cornellappdev.coursegrab.networking.updateSession
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(this)
    }
    private val credentialContext: MutableContextWrapper by lazy {
        MutableContextWrapper(this)
    }

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (preferencesHelper.expiresAt > System.currentTimeMillis() / 1000L) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            val updateToken = preferencesHelper.updateToken
            if (!updateToken.isNullOrBlank()) {
                val updateSession = Endpoint.updateSession(updateToken)

                lifecycleScope.launch {
                    val userSession = try {
                        val typeToken = object : TypeToken<ApiResponse<UserSession>>() {}.type
                        withContext(Dispatchers.IO) {
                            Request.makeRequest<ApiResponse<UserSession>>(
                                updateSession.okHttpRequest(),
                                typeToken
                            )
                        }?.data
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.d(TAG, "Could not resume previous session", e)
                        null
                    }

                    if (userSession != null) verifySession(userSession)
                }
            }
        }

        binding.signInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        lifecycleScope.launch {
            // Step 1: accounts already authorized for this app. Returning users get a
            // streamlined sheet, or no prompt at all when there is exactly one match.
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.default_web_client_id))
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
                .Builder(getString(R.string.default_web_client_id))
                .build()

            when (val result = requestCredential(signInWithGoogleOption)) {
                is CredentialResult.Success -> handleSignIn(result.response)
                CredentialResult.NoneAvailable ->
                    showLoginError("No Google account found. Add one in system settings.")

                CredentialResult.Cancelled, CredentialResult.Failed -> Unit
            }
        }
    }

    private suspend fun requestCredential(option: CredentialOption): CredentialResult {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        // TODO: this runs in lifecycleScope, so rotating while the sheet is up cancels
        // sign-in. Move the call into a ViewModel (viewModelScope) to survive recreation.
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
            showLoginError("Sign-in failed. Please try again.")
            CredentialResult.Failed
        }
    }

    private sealed interface CredentialResult {
        data class Success(val response: GetCredentialResponse) : CredentialResult
        data object NoneAvailable : CredentialResult
        data object Cancelled : CredentialResult
        data object Failed : CredentialResult
    }

    private fun handleSignIn(response: GetCredentialResponse) {
        val credential = response.credential
        when (credential) {
            is CustomCredential -> {
                // GetSignInWithGoogleOption returns the SIWG credential type, while the
                // bottom-sheet GetGoogleIdOption flow returns the plain one. Accept either.
                if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL &&
                    credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL
                ) {
                    Log.e(TAG, "Unexpected credential type: ${credential.type}")
                    showLoginError("Sign-in failed. Please try again.")
                    return
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential: ${credential::class}")
                return
            }
        }

        val googleCredential = try {
            GoogleIdTokenCredential.createFrom(credential.data)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Failed to parse Google ID token", e)
            showLoginError("Sign-in failed. Please try again.")
            return
        }

        if (!isAllowedAccount(googleCredential.id)) {
            showLoginError("Please use a @cornell.edu account")
            clearCredentialState()
            return
        }

        val initializeSession = Endpoint.initializeSession(googleCredential.idToken, null)

        lifecycleScope.launch {
            val userSession = try {
                val typeToken = object : TypeToken<ApiResponse<UserSession>>() {}.type
                withContext(Dispatchers.IO) {
                    Request.makeRequest<ApiResponse<UserSession>>(
                        initializeSession.okHttpRequest(),
                        typeToken
                    )
                }?.data
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize session", e)
                null
            }

            if (userSession == null) {
                showLoginError("Sign-in failed. Please try again.")
                return@launch
            }

            verifySession(userSession)
        }
    }

    private fun isAllowedAccount(email: String): Boolean =
        email.endsWith("@cornell.edu") ||
                email == "appstoreappdev@gmail.com" ||
                email == "coursegrab.droid@gmail.com"

    private fun clearCredentialState() {
        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: ClearCredentialException) {
                Log.w(TAG, "Failed to clear credential state", e)
            }
        }
    }

    private fun showLoginError(message: String) {
        Snackbar.make(binding.loginRootView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun sendRegistrationToServer(token: String?) {
        val sendDeviceToken = Endpoint.deviceToken(
            preferencesHelper.sessionToken.toString(),
            token.toString()
        )

        lifecycleScope.launch {
            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
            val response = withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<Course>>(
                    sendDeviceToken.okHttpRequest(),
                    typeToken
                )
            }

            if (response!!.success)
                Log.d("NotificationService", "sendRegistrationTokenToServer($token)")
        }
    }

    private fun verifySession(userSession: UserSession) {

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

        enableNotificationsStatus()

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun enableNotificationsStatus() {
        val setNotifs = Endpoint.setNotification(
            accessToken = preferencesHelper.sessionToken.toString(),
            notifSetting = "ANDROID"
        )

        lifecycleScope.launch {
            val typeToken = object : TypeToken<ApiResponse<Course>>() {}.type
            withContext(Dispatchers.IO) {
                Request.makeRequest<ApiResponse<Course>>(
                    setNotifs.okHttpRequest(),
                    typeToken
                )
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
