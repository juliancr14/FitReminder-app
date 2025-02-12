package com.cardoppc.fitreminder.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cardoppc.fitreminder.repository.ProgressRepository

class ProgressViewModel(private val context: Context) : ViewModel() {

    private val progressRepository = ProgressRepository(context)

    fun saveProgress(weight: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        progressRepository.saveProgress(weight, onSuccess, onFailure)
    }

    fun fetchProgress(onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: () -> Unit) {
        progressRepository.fetchProgress(onSuccess, onFailure)
    }
}