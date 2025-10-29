package com.paelsam.inventorywidget.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.paelsam.inventorywidget.R
import com.paelsam.inventorywidget.databinding.FragmentDetailBinding
import java.text.NumberFormat
import java.util.*

/**
 * Fragment que muestra los detalles de un producto específico
 * Permite ver la información completa del producto y editar si es necesario
 */
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(requireActivity().application)
    }

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        loadProductDetail()
    }

    /**
     * Carga los detalles del producto basado en el código recibido
     */
    private fun loadProductDetail() {
        val productCode = args.productCode
        viewModel.loadProduct(productCode)
    }

    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                // Llenar los campos con la información del producto
                binding.tvProductName.text = product.name
                binding.tvProductCode.text = "Código: ${product.code}"
                binding.tvUnitPrice.text = "Precio Unitario: ${formatCurrency(product.unitPrice)}"
                binding.tvQuantity.text = "Cantidad: ${product.quantity}"
                binding.tvTotalValue.text = "Valor Total: ${formatCurrency(product.getTotalValue())}"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                // Mostrar un mensaje de error
                binding.tvErrorMessage.visibility = View.VISIBLE
                binding.tvErrorMessage.text = errorMessage
            }
        }
    }

    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            val productCode = args.productCode
            val action = DetailFragmentDirections.actionDetailFragmentToEditFragment(productCode)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            val productCode = args.productCode
            viewModel.deleteProduct(productCode)
            findNavController().navigateUp()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Formatea un número como moneda
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
