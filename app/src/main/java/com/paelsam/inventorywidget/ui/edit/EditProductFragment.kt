package com.paelsam.inventorywidget.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.paelsam.inventorywidget.databinding.FragmentEditProductBinding
import com.paelsam.inventorywidget.data.model.Product

/**
 * Fragment para editar un producto existente
 */
class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProductViewModel by viewModels {
        EditProductViewModelFactory(requireActivity().application)
    }

    private val args: EditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        loadProductData()
    }

    /**
     * Carga los datos del producto a editar
     */
    private fun loadProductData() {
        val productCode = args.productCode
        viewModel.loadProduct(productCode)
    }

    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                binding.etProductName.setText(product.name)
                binding.etUnitPrice.setText(product.unitPrice.toString())
                binding.etQuantity.setText(product.quantity.toString())
            }
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    "Producto actualizado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveProduct()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Valida y guarda los cambios del producto
     */
    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val priceStr = binding.etUnitPrice.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()

        // Validaciones básicas
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese el nombre del producto", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (priceStr.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Ingrese el precio unitario",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese la cantidad", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceStr.toDouble()
            val quantity = quantityStr.toInt()

            if (price < 0) {
                Toast.makeText(
                    requireContext(),
                    "El precio no puede ser negativo",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (quantity < 0) {
                Toast.makeText(
                    requireContext(),
                    "La cantidad no puede ser negativa",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Obtener el código del producto
            val productCode = args.productCode

            // Crear el producto actualizado
            val updatedProduct = Product(
                code = productCode,
                name = name,
                unitPrice = price,
                quantity = quantity
            )

            // Guardar el producto
            viewModel.updateProduct(updatedProduct)

        } catch (e: NumberFormatException) {
            Toast.makeText(
                requireContext(),
                "Verifique que los valores numéricos sean válidos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
