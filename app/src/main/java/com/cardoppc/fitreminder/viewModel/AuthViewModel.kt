package com.cardoppc.fitreminder.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cardoppc.fitreminder.repository.AuthRepository

class AuthViewModel(private val context: Context) : ViewModel() {

    private val authRepository = AuthRepository(context)

    fun createUser(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        authRepository.createUserWithEmailAndPassword(email, password, onSuccess, onFailure)
    }

    fun signInUser(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        authRepository.signInWithEmailAndPassword(email, password, onSuccess, onFailure)
    }

    fun signInWithGoogle(account: com.google.android.gms.auth.api.signin.GoogleSignInAccount, onSuccess: () -> Unit, onFailure: () -> Unit) {
        authRepository.signInWithGoogle(account, onSuccess, onFailure)
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun getFirebaseToken(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        authRepository.getFirebaseToken(onSuccess, onFailure)
    }
}