package com.paelsam.inventorywidget.data.repository

import android.content.Context
import com.paelsam.inventorywidget.data.local.AppDatabase
import com.paelsam.inventorywidget.data.local.ProductDao
import com.paelsam.inventorywidget.data.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern para abstraer el acceso a datos
 * Actúa como intermediario entre el ViewModel y el DAO
 * En MVVM, los ViewModels solo interactúan con Repositories, nunca directamente con DAOs
 */
class ProductRepository(context: Context) {
    private val productDao: ProductDao = AppDatabase.getDatabase(context).productDao()
    
    /**
     * Obtiene todos los productos como Flow para observar cambios
     */
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    
    /**
     * Obtiene el valor total del inventario
     */
    val totalInventoryValue: Flow<Double?> = productDao.getTotalInventoryValue()
    
    /**
     * Obtiene un producto por su código
     */
    suspend fun getProductByCode(code: Int): Product? {
        return productDao.getProductByCode(code)
    }
    
    /**
     * Inserta un nuevo producto
     * Puede lanzar excepción si el código ya existe
     */
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }
    
    /**
     * Actualiza un producto existente
     */
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }
    
    /**
     * Elimina un producto
     */
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
    
    /**
     * Elimina todos los productos
     */
    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }
}
