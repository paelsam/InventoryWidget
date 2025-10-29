package com.paelsam.inventorywidget.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de sesión de usuario usando SharedPreferences
 * Maneja el estado de autenticación del usuario
 */
class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREF_NAME = "inventory_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_LAST_LOGIN = "last_login"
    }
    
    /**
     * Guarda el estado de inicio de sesión del usuario
     */
    fun setLoggedIn(isLoggedIn: Boolean, userName: String = "") {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_USER_NAME, userName)
            putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            apply()
        }
    }
    
    /**
     * Verifica si el usuario está logueado
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Obtiene el nombre del usuario
     */
    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }
    
    /**
     * Obtiene la fecha del último login
     */
    fun getLastLogin(): Long {
        return prefs.getLong(KEY_LAST_LOGIN, 0)
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_NAME)
            apply()
        }
    }
    
    /**
     * Limpia todas las preferencias
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
