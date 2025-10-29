package com.paelsam.inventorywidget.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paelsam.inventorywidget.data.preferences.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login
 * Maneja la lógica de autenticación biométrica
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sessionManager = SessionManager(application)
    
    // LiveData para observar el estado de autenticación
    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState
    
    // LiveData para mostrar mensajes
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    
    init {
        // Verificar si ya existe una sesión activa
        checkExistingSession()
    }
    
    /**
     * Verifica si hay una sesión activa
     */
    private fun checkExistingSession() {
        if (sessionManager.isLoggedIn()) {
            _authenticationState.value = AuthenticationState.Authenticated
        } else {
            _authenticationState.value = AuthenticationState.Unauthenticated
        }
    }
    
    /**
     * Maneja el resultado de la autenticación biométrica
     */
    fun onBiometricAuthenticationSuccess() {
        viewModelScope.launch {
            _authenticationState.value = AuthenticationState.Loading
            // Simular proceso de autenticación
            delay(500)
            sessionManager.setLoggedIn(true, "Usuario")
            _authenticationState.value = AuthenticationState.Authenticated
            _message.value = "Autenticación exitosa"
        }
    }
    
    /**
     * Maneja errores de autenticación biométrica
     */
    fun onBiometricAuthenticationError(error: String) {
        _authenticationState.value = AuthenticationState.Error(error)
        _message.value = error
    }
    
    /**
     * Maneja fallos de autenticación biométrica
     */
    fun onBiometricAuthenticationFailed() {
        _message.value = "Autenticación fallida. Intente nuevamente."
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        sessionManager.logout()
        _authenticationState.value = AuthenticationState.Unauthenticated
    }
}

/**
 * Estados de autenticación
 */
sealed class AuthenticationState {
    object Unauthenticated : AuthenticationState()
    object Loading : AuthenticationState()
    object Authenticated : AuthenticationState()
    data class Error(val message: String) : AuthenticationState()
}