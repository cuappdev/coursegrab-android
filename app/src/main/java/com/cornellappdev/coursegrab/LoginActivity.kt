package com.cornellappdev.coursegrab

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cornellappdev.coursegrab.models.ApiResponse
import com.cornellappdev.coursegrab.models.UserSession
import com.cornellappdev.coursegrab.networking.Endpoint
import com.cornellappdev.coursegrab.networking.Request
import com.cornellappdev.coursegrab.networking.initializeSession
import com.cornellappdev.coursegrab.networking.updateSession
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 10032

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (preferencesHelper.expiresAt > System.currentTimeMillis() / 1000L){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }else{
            if (preferencesHelper.updateToken != null)
            try {
                val updateSession = Endpoint.updateSession(preferencesHelper.updateToken.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    val typeToken = object : TypeToken<ApiResponse<UserSession>>() {}.type
                    val userSession = withContext(Dispatchers.IO) { Request.makeRequest<ApiResponse<UserSession>>(updateSession.okHttpRequest(), typeToken) }!!.data

                    verifySession(userSession)
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // ...
        }
    }

    private fun verifySession(userSession: UserSession){
        if (userSession.session_expiration.isNullOrEmpty() ||
            userSession.session_token.isNullOrEmpty() ||
            userSession.update_token.isNullOrEmpty()){
            Snackbar.make(login_rootView, "Login Failed, Please Try Again", Snackbar.LENGTH_LONG)
            return
        }

        preferencesHelper.sessionToken = userSession.session_token
        preferencesHelper.updateToken = userSession.update_token
        preferencesHelper.expiresAt = userSession.session_expiration.toLong()

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                val initializeSession = Endpoint.initializeSession(account?.idToken.toString())

                CoroutineScope(Dispatchers.Main).launch {
                    val typeToken = object : TypeToken<ApiResponse<UserSession>>() {}.type
                    val userSession = withContext(Dispatchers.IO) { Request.makeRequest<ApiResponse<UserSession>>(initializeSession.okHttpRequest(), typeToken) }!!.data

                    verifySession(userSession)
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }
}
