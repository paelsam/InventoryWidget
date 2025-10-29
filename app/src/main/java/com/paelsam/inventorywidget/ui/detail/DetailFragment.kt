package com.paelsam.inventorywidget.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.paelsam.inventorywidget.databinding.FragmentDetailBinding
import java.text.NumberFormat
import java.util.*

/**
 * Fragment para mostrar el detalle de un producto
 * Permite ver toda la información del producto, editarlo o eliminarlo
 */
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Cargar el producto usando el código recibido como argumento
        viewModel.loadProduct(args.productCode)
        
        setupObservers()
        setupClickListeners()
    }
    
    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        // Observar el producto
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                binding.tvProductCode.text = product.code.toString().padStart(4, '0')
                binding.tvProductName.text = product.name
                binding.tvProductPrice.text = formatCurrency(product.unitPrice)
                binding.tvProductQuantity.text = product.quantity.toString()
                binding.tvProductTotal.text = formatCurrency(product.getTotalValue())
            } else {
                // Si el producto no existe, volver atrás
                Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
        
        // Observar si el producto fue eliminado
        viewModel.productDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }
    
    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            // Navegar a la pantalla de edición
            val action = DetailFragmentDirections.actionDetailFragmentToEditFragment(args.productCode)
            findNavController().navigate(action)
        }
        
        binding.btnDelete.setOnClickListener {
            // Eliminar el producto
            viewModel.deleteProduct()
        }
    }
    
    /**
     * Formatea un número como moneda con separadores de miles y dos decimales
     * Ejemplo: 3326.00 -> $3.326,00
     */
    private fun formatCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        return format.format(value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
