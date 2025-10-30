package com.paelsam.inventorywidget.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paelsam.inventorywidget.R
import com.paelsam.inventorywidget.databinding.FragmentLoginBinding

/**
 * Fragment de Login con autenticación biométrica
 * Sin toolbar según especificaciones
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(requireActivity().application)
    }
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupBiometricAuthentication()
        setupObservers()
        setupClickListeners()
        
        // Configurar la animación Lottie
        binding.animationView.apply {
            setAnimationFromUrl("https://lottie.host/1379c6b3-7ea7-4608-97ee-c7a17792c9f2/goKfHQaaTy.lottie")
            speed = 3f
            playAnimation()
    }
    
    /**
     * Configura la autenticación biométrica
     */
    private fun setupBiometricAuthentication() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    viewModel.onBiometricAuthenticationError(errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.onBiometricAuthenticationSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    viewModel.onBiometricAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("Cancelar")
            .build()
    }
    
    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        viewModel.authenticationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthenticationState.Unauthenticated -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAuthenticate.isEnabled = true
                }
                is AuthenticationState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnAuthenticate.isEnabled = false
                }
                is AuthenticationState.Authenticated -> {
                    binding.progressBar.visibility = View.GONE
                    // Navegar a la pantalla Home
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                is AuthenticationState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAuthenticate.isEnabled = true
                }
            }
        }
        
        viewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {
        binding.btnAuthenticate.setOnClickListener {
            checkBiometricSupport()
        }
    }
    
    /**
     * Verifica si el dispositivo soporta biometría
     */
    private fun checkBiometricSupport() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(requireContext(), 
                    "Este dispositivo no tiene sensor biométrico", 
                    Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(requireContext(), 
                    "El sensor biométrico no está disponible", 
                    Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(requireContext(), 
                    "No hay huellas digitales registradas", 
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
