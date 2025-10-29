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

/**
 * Fragment para editar un producto existente
 * Incluye validaciones en tiempo real según especificaciones
 */
class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EditProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Cargar el producto usando el código recibido como argumento
        viewModel.loadProduct(args.productCode)
        
        setupObservers()
        setupClickListeners()
        setupValidation()
    }
    
    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        // Observar el producto para prellenar los campos
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                binding.etCode.setText(product.code.toString().padStart(4, '0'))
                binding.etName.setText(product.name)
                binding.etPrice.setText(product.unitPrice.toString())
                binding.etQuantity.setText(product.quantity.toString())
                
                // Habilitar el botón de guardar inicialmente
                updateSaveButtonState()
            } else {
                // Si el producto no existe, volver atrás
                Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
        
        // Observar el resultado de la actualización
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UpdateResult.Success -> {
                    Toast.makeText(requireContext(), "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UpdateResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    binding.btnSave.isEnabled = true
                }
                null -> {
                    // No hacer nada
                }
            }
        }
    }
    
    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            binding.btnSave.isEnabled = false
            
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString()
            val quantity = binding.etQuantity.text.toString()
            
            viewModel.updateProduct(name, price, quantity)
        }
    }
    
    /**
     * Configura la validación en tiempo real de los campos
     */
    private fun setupValidation() {
        // El botón solo se habilita si todos los campos tienen contenido
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateSaveButtonState()
            }
        }
        
        binding.etName.addTextChangedListener(textWatcher)
        binding.etPrice.addTextChangedListener(textWatcher)
        binding.etQuantity.addTextChangedListener(textWatcher)
    }
    
    /**
     * Actualiza el estado del botón Guardar
     */
    private fun updateSaveButtonState() {
        val isFormValid = binding.etName.text.toString().isNotBlank() &&
                         binding.etPrice.text.toString().isNotBlank() &&
                         binding.etQuantity.text.toString().isNotBlank()
        
        binding.btnSave.isEnabled = isFormValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetUpdateResult()
        _binding = null
    }
}
