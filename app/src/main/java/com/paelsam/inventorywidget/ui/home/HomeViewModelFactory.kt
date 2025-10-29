package com.paelsam.inventorywidget.ui.home

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

/**
 * Factory para crear instancias de HomeViewModel
 * Proporciona el Application que requiere el constructor del ViewModel
 */
class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(application) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}
