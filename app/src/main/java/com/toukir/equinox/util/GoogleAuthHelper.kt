package com.toukir.equinox.util

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object GoogleAuthHelper {

    private fun getWebClientId(context: Context): String? {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) context.getString(resId) else null
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient? {
        val webClientId = getWebClientId(context) ?: return null
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun signInWithGoogleIntent(intent: Intent?): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val idToken = account.idToken ?: return Result.failure(Exception("Google Sign-In returned null ID token"))

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val email = authResult.user?.email ?: account.email ?: "Signed in"
            Result.success(email)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut(context: Context, onComplete: () -> Unit) {
        try {
            FirebaseAuth.getInstance().signOut()
            getGoogleSignInClient(context)?.signOut()?.addOnCompleteListener {
                onComplete()
            } ?: onComplete()
        } catch (e: Exception) {
            onComplete()
        }
    }
}
