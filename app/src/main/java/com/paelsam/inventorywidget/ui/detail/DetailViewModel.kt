package com.paelsam.inventorywidget.ui.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.paelsam.inventorywidget.data.model.Product
import com.paelsam.inventorywidget.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel para el DetailFragment
 * Maneja la lógica de carga y eliminación de productos
 */
class DetailViewModel(application: Application) : ViewModel() {

    private val repository = ProductRepository(application)

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    /**
     * Carga un producto específico por su código
     */
    fun loadProduct(productCode: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val foundProduct = repository.getProductByCode(productCode)
                _product.value = foundProduct
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar el producto: ${e.message}"
            }
        }
    }

    /**
     * Elimina un producto por su código
     */
    fun deleteProduct(productCode: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val product = repository.getProductByCode(productCode)
                if (product != null) {
                    repository.deleteProduct(product)
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar el producto: ${e.message}"
            }
        }
    }
}

/**
 * Factory para crear instancias de DetailViewModel
 */
class DetailViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            DetailViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
