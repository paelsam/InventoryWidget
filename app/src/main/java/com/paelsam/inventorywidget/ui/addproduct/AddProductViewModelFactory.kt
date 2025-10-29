package com.paelsam.inventorywidget.ui.addproduct

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

/**
 * Factory para crear instancias de AddProductViewModel
 * Proporciona el Application que requiere el constructor del ViewModel
 */
class AddProductViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddProductViewModel::class.java)) {
            AddProductViewModel(application) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}
