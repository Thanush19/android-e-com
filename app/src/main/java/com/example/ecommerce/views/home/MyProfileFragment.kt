package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.databinding.FragmentMyProfileBinding
import com.example.ecommerce.views.adapters.OrderDropdownAdapter
import com.example.ecommerce.views.auth.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by activityViewModels()

    @Inject
    lateinit var ordersRepository: OrdersRepository

    @Inject
    lateinit var productRepository: ProductRepository

    private lateinit var orderDropdownAdapter: OrderDropdownAdapter
    private var popupWindow: PopupWindow? = null

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

        orderDropdownAdapter = OrderDropdownAdapter { productId ->
            // Navigate to product details when a product is clicked
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToProductDetailsFragment(productId)
            popupWindow?.dismiss() // Dismiss the popup before navigating
            findNavController().navigate(action)
        }

        vm.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvProfileGreeting.text = "Hi, ${user.userName}"
            } else {
                binding.tvProfileGreeting.text = "Hi, Guest"
            }
        }

        binding.tvMyOrders.setOnClickListener {
            vm.currentUser.value?.let { user ->
                lifecycleScope.launch {
                    val orders = ordersRepository.getOrdersByUser(user.id).firstOrNull()
                    if (orders.isNullOrEmpty()) {
                        Toast.makeText(context, "No orders found", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val productIds = orders.flatMap { it.productIds }.distinct()
                    val products = mutableListOf<com.example.ecommerce.data.model.Product>()
                    for (productId in productIds) {
                        productRepository.getProductById(productId)?.let { product ->
                            products.add(product)
                        }
                    }

                    if (products.isEmpty()) {
                        Toast.makeText(context, "No product details available", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    showOrdersDropdown(products)
                }
            } ?: run {
                Toast.makeText(context, "Please log in to view orders", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogout.setOnClickListener {
            vm.logout()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showOrdersDropdown(products: List<com.example.ecommerce.data.model.Product>) {
        popupWindow?.dismiss()

        val recyclerView = androidx.recyclerview.widget.RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderDropdownAdapter
        }
        orderDropdownAdapter.submitList(products)

        popupWindow = PopupWindow(
            recyclerView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            elevation = 8f
        }

        // Show below tvMyOrders
        popupWindow?.showAsDropDown(binding.tvMyOrders)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        popupWindow?.dismiss()
        popupWindow = null
        _binding = null
    }

    companion object {
        fun newInstance() = MyProfileFragment()
    }
}
