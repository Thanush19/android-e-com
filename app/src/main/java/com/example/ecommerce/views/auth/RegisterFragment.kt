package com.example.ecommerce.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            viewModel.register(username, password, confirmPassword)
        }

        binding.tvLoginPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is AuthState.Loading -> {
                            _binding?.progressBar?.visibility = View.VISIBLE
                            _binding?.btnRegister?.isEnabled = false
                        }
                        is AuthState.Success -> {
                            _binding?.progressBar?.visibility = View.GONE
                            _binding?.btnRegister?.isEnabled = true
                            navigateToHome()
                        }
                        is AuthState.Error -> {
                            _binding?.progressBar?.visibility = View.GONE
                            _binding?.btnRegister?.isEnabled = true
                            _binding?.let {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()

        try {
            if (findNavController().currentDestination?.id == R.id.registerFragment) {
                findNavController().navigate(
                    R.id.action_registerFragment_to_myFeedFragment,
                    null,
                    navOptions
                )
            }
        } catch (e: Exception) {
            findNavController().navigate(R.id.loginFragment)
            findNavController().navigate(R.id.action_loginFragment_to_myFeedFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}