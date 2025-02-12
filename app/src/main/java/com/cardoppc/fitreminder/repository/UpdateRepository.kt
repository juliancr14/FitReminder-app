package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore

class UpdateRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    fun updateUserProfile(userInfo: Map<String, Any>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userId = "user_id" // Obtener el ID del usuario actual
        firestore.collection("users").document(userId)
            .set(userInfo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}