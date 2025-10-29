package com.paelsam.inventorywidget.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.paelsam.inventorywidget.R
import com.paelsam.inventorywidget.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.util.*

/**
 * Fragment Home que muestra la lista de productos del inventario
 * Incluye un FAB para agregar nuevos productos
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    /**
     * Configura el RecyclerView con su adaptador
     */
    private fun setupRecyclerView() {
        adapter = ProductAdapter { product ->
            // Navegar al detalle del producto al hacer clic
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(product.code)
            findNavController().navigate(action)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }
    
    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        // Observar la lista de productos
        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            
            // Mostrar mensaje si no hay productos
            if (products.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
        
        // Observar el saldo total
        viewModel.totalInventoryValue.observe(viewLifecycleOwner) { total ->
            val formattedTotal = formatCurrency(total ?: 0.0)
            binding.tvTotalValue.text = "Saldo Total: $formattedTotal"
        }
    }
    
    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            // Navegar a la pantalla de agregar producto
            findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
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
