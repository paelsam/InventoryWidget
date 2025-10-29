package com.paelsam.inventorywidget.ui.edit

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
 * ViewModel para editar un producto existente
 * Incluye validaciones según especificaciones
 */
class EditProductViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    
    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product
    
    private val _updateResult = MutableLiveData<UpdateResult>()
    val updateResult: LiveData<UpdateResult> = _updateResult
    
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
     * Valida y actualiza el producto
     */
    fun updateProduct(name: String, price: String, quantity: String) {
        viewModelScope.launch {
            try {
                val currentProduct = _product.value
                if (currentProduct == null) {
                    _updateResult.value = UpdateResult.Error("Producto no encontrado")
                    return@launch
                }
                
                // Validación de nombre: cadena de texto, máximo 40 caracteres
                if (name.isBlank()) {
                    _updateResult.value = UpdateResult.Error("El nombre no puede estar vacío")
                    return@launch
                }
                
                if (name.length > 40) {
                    _updateResult.value = UpdateResult.Error("El nombre no puede exceder 40 caracteres")
                    return@launch
                }
                
                // Validación de precio
                val priceValue = price.toDoubleOrNull()
                if (priceValue == null || priceValue <= 0) {
                    _updateResult.value = UpdateResult.Error("El precio debe ser un número mayor a 0")
                    return@launch
                }
                
                // Validación de cantidad
                val quantityValue = quantity.toIntOrNull()
                if (quantityValue == null || quantityValue < 0) {
                    _updateResult.value = UpdateResult.Error("La cantidad debe ser un número mayor o igual a 0")
                    return@launch
                }
                
                // Actualizar el producto
                val updatedProduct = currentProduct.copy(
                    name = name,
                    unitPrice = priceValue,
                    quantity = quantityValue
                )
                
                repository.updateProduct(updatedProduct)
                _updateResult.value = UpdateResult.Success
                
            } catch (e: Exception) {
                _updateResult.value = UpdateResult.Error("Error al actualizar: ${e.message}")
            }
        }
    }
    
    /**
     * Resetea el resultado de actualización
     */
    fun resetUpdateResult() {
        _updateResult.value = null
    }
}

/**
 * Resultado de la operación de actualización
 */
sealed class UpdateResult {
    object Success : UpdateResult()
    data class Error(val message: String) : UpdateResult()
}
