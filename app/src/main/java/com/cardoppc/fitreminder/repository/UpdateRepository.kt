package com.cardoppc.fitreminder.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

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
                .addOnSuccessListener {
                    // Si el peso fue actualizado, guardarlo en la colección "progress"
                    if (userInfo.containsKey("weight")) {
                        saveWeightToProgress(userInfo["weight"].toString(), onSuccess, onFailure)
                    } else {
                        onSuccess()
                    }
                }
                .addOnFailureListener { onFailure() }
        } else {
            onFailure()
        }
    }

    fun saveWeightToProgress(weight: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            // Actualizar el peso en el perfil del usuario
            firestore.collection("users").document(userEmail)
                .update("weight", weight)
                .addOnSuccessListener {
                    // Guardar el peso en el historial
                    val progressData = mapOf(
                        "weight" to weight,
                        "timestamp" to Date()
                    )
                    firestore.collection("users").document(userEmail)
                        .collection("progress")
                        .add(progressData)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure() }
                }
                .addOnFailureListener { onFailure() }
        } else {
            onFailure()
        }
    }

    fun fetchProgressHistory(onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: () -> Unit) {
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            firestore.collection("users").document(userEmail)
                .collection("progress")
                .orderBy("timestamp") // Ordenar por fecha
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val progressList = querySnapshot.documents.mapNotNull { it.data }
                    onSuccess(progressList)
                }
                .addOnFailureListener { onFailure() }
        } else {
            onFailure()
        }
    }
}