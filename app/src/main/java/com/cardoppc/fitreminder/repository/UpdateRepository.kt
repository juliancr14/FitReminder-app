package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore

class UpdateRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchUserProfile(onSuccess: (Map<String, Any>) -> Unit, onFailure: () -> Unit) {
        val userId = "user_id" // Obtener el ID del usuario actual
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess(document.data ?: emptyMap())
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener { onFailure() }
    }

    fun updateUserProfile(userInfo: Map<String, Any>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userId = "user_id" // Obtener el ID del usuario actual
        firestore.collection("users").document(userId)
            .set(userInfo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}