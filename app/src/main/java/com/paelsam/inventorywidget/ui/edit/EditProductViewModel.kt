package com.paelsam.inventorywidget.ui.edit

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
 * ViewModel para el EditProductFragment
 * Maneja la lógica de carga y actualización de productos
 */
class EditProductViewModel(application: Application) : ViewModel() {

    private val repository = ProductRepository(application)

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess

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
     * Actualiza un producto existente
     */
    fun updateProduct(product: Product) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                repository.updateProduct(product)
                _saveSuccess.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar el producto: ${e.message}"
                _saveSuccess.value = false
            }
        }
    }
}

/**
 * Factory para crear instancias de EditProductViewModel
 */
class EditProductViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EditProductViewModel::class.java)) {
            EditProductViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
