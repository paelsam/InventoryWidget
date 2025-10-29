package com.paelsam.inventorywidget.ui.addproduct

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
 * ViewModel para agregar un nuevo producto
 * Incluye validaciones según especificaciones
 */
class AddProductViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult
    
    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
    }
    
    /**
     * Valida y guarda un nuevo producto
     */
    fun saveProduct(code: String, name: String, price: String, quantity: String) {
        viewModelScope.launch {
            try {
                // Validación de código: solo números, máximo 4 dígitos
                if (code.isBlank()) {
                    _saveResult.value = SaveResult.Error("El código no puede estar vacío")
                    return@launch
                }
                
                if (!code.matches(Regex("^\\d{1,4}$"))) {
                    _saveResult.value = SaveResult.Error("El código debe contener solo números (máximo 4 dígitos)")
                    return@launch
                }
                
                // Validación de nombre: cadena de texto, máximo 40 caracteres
                if (name.isBlank()) {
                    _saveResult.value = SaveResult.Error("El nombre no puede estar vacío")
                    return@launch
                }
                
                if (name.length > 40) {
                    _saveResult.value = SaveResult.Error("El nombre no puede exceder 40 caracteres")
                    return@launch
                }
                
                // Validación de precio
                val priceValue = price.toDoubleOrNull()
                if (priceValue == null || priceValue <= 0) {
                    _saveResult.value = SaveResult.Error("El precio debe ser un número mayor a 0")
                    return@launch
                }
                
                // Validación de cantidad
                val quantityValue = quantity.toIntOrNull()
                if (quantityValue == null || quantityValue < 0) {
                    _saveResult.value = SaveResult.Error("La cantidad debe ser un número mayor o igual a 0")
                    return@launch
                }
                
                // Verificar si el código ya existe
                val existingProduct = repository.getProductByCode(code.toInt())
                if (existingProduct != null) {
                    _saveResult.value = SaveResult.Error("Ya existe un producto con el código $code")
                    return@launch
                }
                
                // Crear y guardar el producto
                val product = Product(
                    code = code.toInt(),
                    name = name,
                    unitPrice = priceValue,
                    quantity = quantityValue
                )
                
                repository.insertProduct(product)
                _saveResult.value = SaveResult.Success
                
            } catch (e: Exception) {
                _saveResult.value = SaveResult.Error("Error al guardar: ${e.message}")
            }
        }
    }
    
    /**
     * Resetea el resultado de guardado
     */
    fun resetSaveResult() {
        _saveResult.value = null
    }
}

/**
 * Resultado de la operación de guardado
 */
sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}
