package com.paelsam.inventorywidget.ui.addproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paelsam.inventorywidget.databinding.FragmentAddProductBinding

/**
 * Fragment para agregar un nuevo producto
 * Incluye validaciones en tiempo real según especificaciones
 */
class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddProductViewModel by viewModels {
        AddProductViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupClickListeners()
        setupValidation()
    }
    
    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SaveResult.Success -> {
                    Toast.makeText(requireContext(), "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is SaveResult.Error -> {
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
            
            val code = binding.etCode.text.toString()
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString()
            val quantity = binding.etQuantity.text.toString()
            
            viewModel.saveProduct(code, name, price, quantity)
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
        
        binding.etCode.addTextChangedListener(textWatcher)
        binding.etName.addTextChangedListener(textWatcher)
        binding.etPrice.addTextChangedListener(textWatcher)
        binding.etQuantity.addTextChangedListener(textWatcher)
    }
    
    /**
     * Actualiza el estado del botón Guardar
     */
    private fun updateSaveButtonState() {
        val isFormValid = binding.etCode.text.toString().isNotBlank() &&
                         binding.etName.text.toString().isNotBlank() &&
                         binding.etPrice.text.toString().isNotBlank() &&
                         binding.etQuantity.text.toString().isNotBlank()
        
        binding.btnSave.isEnabled = isFormValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetSaveResult()
        _binding = null
    }
}
