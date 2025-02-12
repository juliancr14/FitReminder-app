package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun fetchUserProfile(onSuccess: (Map<String, Any>) -> Unit, onFailure: () -> Unit) {
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            firestore.collection("users").document(userEmail)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        onSuccess(document.data ?: emptyMap())
                    } else {
                        onFailure()
                    }
                }
                .addOnFailureListener { onFailure() }
        } else {
            onFailure()
        }
    }

    fun updateUserProfile(userInfo: Map<String, Any>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            firestore.collection("users").document(userEmail)
                .update(userInfo)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure() }
        } else {
            onFailure()
        }
    }

    fun saveProgress(weight: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            firestore.collection("users").document(userEmail)
                .update("weight", weight)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure() }
        } else {
            onFailure()
        }
    }
}