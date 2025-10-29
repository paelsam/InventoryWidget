package com.paelsam.inventorywidget.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paelsam.inventorywidget.data.model.Product
import com.paelsam.inventorywidget.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.*

/**
 * Adaptador para mostrar la lista de productos en un RecyclerView
 * Usa ListAdapter con DiffUtil para optimizar las actualizaciones
 */
class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, onItemClick: (Product) -> Unit) {
            binding.apply {
                tvProductCode.text = "Código: ${product.code}"
                tvProductName.text = product.name
                tvProductPrice.text = formatCurrency(product.unitPrice)
                tvProductQuantity.text = "Cantidad: ${product.quantity}"
                tvProductTotal.text = "Total: ${formatCurrency(product.getTotalValue())}"
                
                root.setOnClickListener { onItemClick(product) }
            }
        }
        
        private fun formatCurrency(value: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            return format.format(value)
        }
    }

    /**
     * DiffUtil callback para comparar productos
     * Optimiza las actualizaciones del RecyclerView
     */
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
