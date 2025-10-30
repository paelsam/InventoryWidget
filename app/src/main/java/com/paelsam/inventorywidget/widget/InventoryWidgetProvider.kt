package com.paelsam.inventorywidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.paelsam.inventorywidget.R
import com.paelsam.inventorywidget.data.repository.ProductRepository
import com.paelsam.inventorywidget.ui.login.LoginFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InventoryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            TOGGLE_VISIBILITY_ACTION -> {
                val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
                val isVisible = !prefs.getBoolean(KEY_BALANCE_VISIBLE, true)
                prefs.edit().putBoolean(KEY_BALANCE_VISIBLE, isVisible).apply()
                
                // Actualizar todos los widgets
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, InventoryWidgetProvider::class.java)
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    companion object {
        private const val WIDGET_PREFS = "widget_prefs"
        private const val KEY_BALANCE_VISIBLE = "balance_visible"
        private const val TOGGLE_VISIBILITY_ACTION = "com.paelsam.inventorywidget.TOGGLE_VISIBILITY"
        
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_inventory)
            val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
            val isBalanceVisible = prefs.getBoolean(KEY_BALANCE_VISIBLE, true)

            // Configurar el ícono de ojo según el estado
            views.setImageViewResource(
                R.id.widget_eye_icon,
                if (isBalanceVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
            )

            // Configurar el PendingIntent para alternar la visibilidad
            val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
                action = TOGGLE_VISIBILITY_ACTION
            }
            val togglePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePendingIntent)

            // Configurar el intent para abrir la pantalla de login
            val loginIntent = Intent(context, LoginFragment::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val loginPendingIntent = PendingIntent.getActivity(
                context,
                0,
                loginIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_manage_inventory, loginPendingIntent)

            // Cargar datos del inventario desde la base de datos
            val repository = ProductRepository(context)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val totalInventory = repository.totalInventoryValue.first()
                    
                    // Formatear el saldo con separador de miles y dos decimales
                    if (totalInventory != null && totalInventory > 0) {
                        val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                        val formattedValue = if (isBalanceVisible) {
                            numberFormat.format(totalInventory)
                        } else {
                            "$ ******"
                        }
                        views.setTextViewText(R.id.widget_balance_text, formattedValue)
                    } else {
                        views.setTextViewText(R.id.widget_balance_text, "$ ******")
                    }
                } catch (e: Exception) {
                    views.setTextViewText(R.id.widget_balance_text, "$ ******")
                }
                
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
