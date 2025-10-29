package com.paelsam.inventorywidget.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.paelsam.inventorywidget.data.local.AppDatabase
import com.paelsam.inventorywidget.data.model.Product
import com.paelsam.inventorywidget.data.repository.ProductRepository

/**
 * ViewModel para la pantalla Home (lista de productos)
 * Observa la lista de productos y el saldo total del inventario
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    
    // LiveData que contiene todos los productos
    val allProducts: LiveData<List<Product>>
    
    // LiveData que contiene el saldo total del inventario
    val totalInventoryValue: LiveData<Double?>
    
    init {
        repository = ProductRepository(application)
        
        // Convierte Flow a LiveData para observar en la UI
        allProducts = repository.allProducts.asLiveData()
        totalInventoryValue = repository.totalInventoryValue.asLiveData()
    }
}
