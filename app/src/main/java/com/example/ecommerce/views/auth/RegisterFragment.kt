package com.example.ecommerce.views.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.example.ecommerce.views.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

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
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is RegisterState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                is RegisterState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

