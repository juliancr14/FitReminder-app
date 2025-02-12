package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class AuthRepository(private val context: Context) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun createUserWithEmailAndPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        // Crear un documento en Firestore con el email como ID
                        firestore.collection("users").document(user.email!!)
                            .set(mapOf("email" to user.email))
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure() }
                    } else {
                        onFailure()
                    }
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
                    val user = task.result?.user
                    if (user != null) {
                        // Crear o actualizar el documento en Firestore con el email como ID
                        firestore.collection("users").document(user.email!!)
                            .set(mapOf("email" to user.email))
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure() }
                    } else {
                        onFailure()
                    }
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
