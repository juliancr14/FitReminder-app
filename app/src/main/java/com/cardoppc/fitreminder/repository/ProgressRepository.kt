package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore

class ProgressRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveProgress(weight: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userId = "user_id" // Obtener el ID del usuario actual
        firestore.collection("users").document(userId).collection("progress")
            .add(mapOf("weight" to weight))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    fun fetchProgress(onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: () -> Unit) {
        val userId = "user_id" // Obtener el ID del usuario actual
        firestore.collection("users").document(userId).collection("progress")
            .get()
            .addOnSuccessListener { documents ->
                val progressList = documents.map { it.data }
                onSuccess(progressList)
            }
            .addOnFailureListener { onFailure() }
    }
}