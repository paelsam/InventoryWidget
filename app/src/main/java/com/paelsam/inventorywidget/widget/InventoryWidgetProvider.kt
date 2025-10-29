package com.paelsam.inventorywidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.paelsam.inventorywidget.R
import com.paelsam.inventorywidget.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    companion object {
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_inventory)

            // Cargar datos del inventario desde la base de datos
            val repository = ProductRepository(context)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val totalInventory = repository.totalInventoryValue.first()
                    
                    // Actualizar el widget con el saldo
                    if (totalInventory != null && totalInventory > 0) {
                        views.setTextViewText(
                            R.id.widget_balance_text,
                            String.format("$%.2f", totalInventory)
                        )
                    } else {
                        views.setTextViewText(R.id.widget_balance_text, "$****")
                    }
                } catch (e: Exception) {
                    views.setTextViewText(R.id.widget_balance_text, "$****")
                }
                
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
