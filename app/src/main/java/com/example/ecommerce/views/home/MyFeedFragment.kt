package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.databinding.FragmentMyFeedBinding
import com.example.ecommerce.viewModels.HomeViewModel
import com.example.ecommerce.views.adapters.HorizontalProductAdapter
import com.example.ecommerce.views.adapters.ProductAdapter

class MyFeedFragment : Fragment() {

    private var _binding: FragmentMyFeedBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by activityViewModels()
    private val productAdapter by lazy { ProductAdapter().apply { setOnProductClickListener { showToast(it.title) } }  }
    private val horizontalProductAdapter by lazy { HorizontalProductAdapter().apply { setOnProductClickListener { showToast(it.title) } } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        binding.rvHorizontalProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = horizontalProductAdapter
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun observeViewModel() {
        vm.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
            horizontalProductAdapter.updateProducts(products)
        }

        vm.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let { showToast(it) }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), "Selected: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MyFeedFragment()
    }
}
