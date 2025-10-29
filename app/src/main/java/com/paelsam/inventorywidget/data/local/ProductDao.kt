package com.paelsam.inventorywidget.data.local

import androidx.room.*
import com.paelsam.inventorywidget.data.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la entidad Product
 * Utiliza Flow para observar cambios en tiempo real
 * Todas las operaciones son suspend functions para usar con corrutinas
 */
@Dao
interface ProductDao {
    
    /**
     * Obtiene todos los productos ordenados por código
     * Flow permite observar cambios en tiempo real
     */
    @Query("SELECT * FROM products ORDER BY code ASC")
    fun getAllProducts(): Flow<List<Product>>
    
    /**
     * Obtiene un producto por su código
     */
    @Query("SELECT * FROM products WHERE code = :productCode")
    suspend fun getProductByCode(productCode: Int): Product?
    
    /**
     * Inserta un nuevo producto
     * OnConflictStrategy.ABORT lanza excepción si el código ya existe
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: Product)
    
    /**
     * Actualiza un producto existente
     */
    @Update
    suspend fun updateProduct(product: Product)
    
    /**
     * Elimina un producto
     */
    @Delete
    suspend fun deleteProduct(product: Product)
    
    /**
     * Calcula el saldo total del inventario (suma de precio * cantidad de todos los productos)
     */
    @Query("SELECT SUM(unitPrice * quantity) FROM products")
    fun getTotalInventoryValue(): Flow<Double?>
    
    /**
     * Elimina todos los productos (útil para testing)
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
