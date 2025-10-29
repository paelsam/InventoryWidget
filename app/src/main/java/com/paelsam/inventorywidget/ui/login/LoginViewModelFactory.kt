package com.paelsam.inventorywidget.ui.login

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

/**
 * Factory para crear instancias de LoginViewModel
 * Proporciona el Application que requiere el constructor del ViewModel
 */
class LoginViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(application) as T
        } else {
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}
