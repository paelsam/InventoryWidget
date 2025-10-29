package com.paelsam.inventorywidget.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Product para Room Database
 * Representa un producto en el inventario
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val code: Int, // Código del producto (máximo 4 dígitos)
    
    val name: String, // Nombre del artículo (máximo 40 caracteres)
    
    val unitPrice: Double, // Precio unitario del producto
    
    val quantity: Int // Cantidad en inventario
) {
    /**
     * Calcula el valor total del producto (precio unitario * cantidad)
     */
    fun getTotalValue(): Double = unitPrice * quantity
}
