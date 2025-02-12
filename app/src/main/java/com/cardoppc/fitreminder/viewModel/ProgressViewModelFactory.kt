package com.cardoppc.fitreminder.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProgressViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
