package com.cardoppc.fitreminder.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cardoppc.fitreminder.repository.UpdateRepository

class UpdateViewModel(private val context: Context) : ViewModel() {

    private val updateRepository = UpdateRepository(context)

    fun updateUserProfile(userInfo: Map<String, Any>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        updateRepository.updateUserProfile(userInfo, onSuccess, onFailure)
    }
}