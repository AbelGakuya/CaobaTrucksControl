package com.example.caobatruckscontrol.presentation.composables.sign_in

import android.content.Context
import android.util.Log
import com.example.caobatruckscontrol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthClient @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth
) {
    private val TAG = "GoogleAuthClient"

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount?) {
        if (account == null) {
            Log.e(TAG, "signInWithGoogle: Google sign-in account is null")
            throw Exception("Google sign-in account is null")
        }

        Log.d(TAG, "signInWithGoogle: attempting sign-in with account: ${account.email}")
        try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).await()
            Log.d(TAG, "signInWithGoogle: successful")
        } catch (e: Exception) {
            Log.e(TAG, "signInWithGoogle: failed", e)
            throw e
        }
    }

    fun getSignInIntent() = googleSignInClient.signInIntent
}
