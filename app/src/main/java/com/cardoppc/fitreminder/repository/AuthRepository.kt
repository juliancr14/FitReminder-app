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
                        // Guardar los datos del usuario en Firestore
                        saveUserDataToFirestore(user.email ?: "", onSuccess, onFailure)
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
                    val user = task.result?.user
                    if (user != null) {
                        // Verificar si el usuario ya tiene datos en Firestore
                        fetchUserDataFromFirestore(user.email ?: "", onSuccess, onFailure)
                    } else {
                        onFailure()
                    }
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
                        // Guardar los datos del usuario en Firestore
                        saveUserDataToFirestore(user.email ?: "", onSuccess, onFailure)
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

    private fun saveUserDataToFirestore(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection("users").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Crear el documento solo si no existe
                    firestore.collection("users").document(email)
                        .set(mapOf("email" to email))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure() }
                } else {
                    onSuccess()
                }
            }
            .addOnFailureListener { onFailure() }
    }

    private fun fetchUserDataFromFirestore(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection("users").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess()
                } else {
                    // Si no existe, crear el documento
                    saveUserDataToFirestore(email, onSuccess, onFailure)
                }
            }
            .addOnFailureListener { onFailure() }
    }
}
