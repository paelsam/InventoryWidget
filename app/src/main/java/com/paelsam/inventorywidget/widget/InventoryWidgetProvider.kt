package com.paelsam.inventorywidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.paelsam.inventorywidget.R
import com.paelsam.inventorywidget.data.repository.ProductRepository
import com.paelsam.inventorywidget.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

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
            ACTION_TOGGLE_EYE -> {
                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (appWidgetId != -1) {
                    toggleEyeState(context, appWidgetId)
                }
            }
            ACTION_OPEN_APP -> {
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(mainIntent)
            }
        }
    }

    private fun toggleEyeState(context: Context, appWidgetId: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentState = sharedPreferences.getBoolean("eye_open_$appWidgetId", true)
        val newState = !currentState
        
        sharedPreferences.edit().putBoolean("eye_open_$appWidgetId", newState).apply()
        
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {
        private const val PREFS_NAME = "InventoryWidgetPrefs"
        private const val ACTION_TOGGLE_EYE = "com.paelsam.inventorywidget.TOGGLE_EYE"
        private const val ACTION_OPEN_APP = "com.paelsam.inventorywidget.OPEN_APP"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_inventory)

            // Obtener el estado del ojo desde SharedPreferences
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val eyeIsOpen = sharedPreferences.getBoolean("eye_open_$appWidgetId", true)

            // Configurar el icono del ojo basado en su estado
            views.setImageViewResource(
                R.id.widget_eye_icon,
                if (eyeIsOpen) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
            )

            // Configurar el click listener para el ojo
            val eyeIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_EYE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val eyePendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                appWidgetId,
                eyeIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_eye_icon, eyePendingIntent)

            // Configurar el click listener para "Gestionar Inventario"
            val appIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
                action = ACTION_OPEN_APP
            }
            val appPendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                appWidgetId + 1000,
                appIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_manage_container, appPendingIntent)

            // Actualizar el widget inmediatamente con la información inicial
            if (eyeIsOpen) {
                views.setTextViewText(R.id.widget_balance_text, "$****")
            } else {
                views.setTextViewText(R.id.widget_balance_text, "$****")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)

            // Cargar datos del inventario desde la base de datos en un hilo de fondo
            thread(start = true) {
                try {
                    val repository = ProductRepository(context)
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val totalInventory = repository.totalInventoryValue.first()
                            
                            // Actualizar el widget con el saldo o asteriscos basado en el estado del ojo
                            if (eyeIsOpen) {
                                if (totalInventory != null && totalInventory > 0) {
                                    views.setTextViewText(
                                        R.id.widget_balance_text,
                                        String.format("$%.2f", totalInventory)
                                    )
                                } else {
                                    views.setTextViewText(R.id.widget_balance_text, "$0.00")
                                }
                            } else {
                                views.setTextViewText(R.id.widget_balance_text, "$****")
                            }
                            
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

