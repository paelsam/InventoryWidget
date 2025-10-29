package com.paelsam.inventorywidget.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paelsam.inventorywidget.data.local.AppDatabase
import com.paelsam.inventorywidget.data.model.Product
import com.paelsam.inventorywidget.data.repository.ProductRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de detalle del producto
 * Maneja la carga y eliminación de un producto específico
 */
class DetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    
    // LiveData que contiene el producto actual
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product
    
    // LiveData que indica si el producto fue eliminado
    private val _productDeleted = MutableLiveData<Boolean>()
    val productDeleted: LiveData<Boolean> = _productDeleted
    
    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }
    
    /**
     * Carga un producto por su código
     */
    fun loadProduct(code: Int) {
        viewModelScope.launch {
            val product = repository.getProductByCode(code)
            _product.value = product
        }
    }
    
    /**
     * Elimina el producto actual
     */
    fun deleteProduct() {
        viewModelScope.launch {
            _product.value?.let { product ->
                repository.deleteProduct(product)
                _productDeleted.value = true
            }
        }
    }
}
