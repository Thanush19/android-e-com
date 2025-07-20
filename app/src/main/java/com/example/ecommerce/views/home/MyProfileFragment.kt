package com.example.ecommerce.views.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.ecommerce.databinding.FragmentMyProfileBinding
import com.example.ecommerce.viewModels.HomeViewModel
import com.example.ecommerce.views.auth.LoginActivity

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvProfileGreeting.text = "Hi, ${user.userName}"
            }
        }

        binding.tvMyCart.setOnClickListener {
            Toast.makeText(context, "My Cart clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tvMyOrders.setOnClickListener {
            Toast.makeText(context, "Orders clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tvLogout.setOnClickListener {
            vm.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MyProfileFragment()
    }
}
