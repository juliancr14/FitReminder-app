package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging

class AuthRepository(private val context: Context) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createUserWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getFirebaseToken(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result)
            } else {
                onFailure()
            }
        }
    }
}
