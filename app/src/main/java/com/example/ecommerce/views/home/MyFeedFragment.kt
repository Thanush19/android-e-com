package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.databinding.FragmentMyFeedBinding
import com.example.ecommerce.databinding.PaginationButtonsBinding
import com.example.ecommerce.views.adapters.ProductAdapter
import com.example.ecommerce.views.adapters.ProductAdapter.LayoutType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFeedFragment : Fragment() {

    private var _binding: FragmentMyFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var paginationBinding: PaginationButtonsBinding

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var verticalProductAdapter: ProductAdapter
    private lateinit var horizontalProductAdapter: ProductAdapter

    private var currentPage = 0
    private val itemsPerPage = 4
    private var totalProducts = 0

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
        paginationBinding = binding.paginationButtons

        paginationBinding.root.visibility = View.GONE
        binding.btnSeeMore.visibility = View.GONE

        setupRecyclerViews()
        setupPaginationButtons()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        horizontalProductAdapter = ProductAdapter(LayoutType.HORIZONTAL) { productId ->
            navigateToProductDetails(productId)
        }

        verticalProductAdapter = ProductAdapter(LayoutType.VERTICAL) { productId ->
            navigateToProductDetails(productId)
        }

        binding.rvHorizontalProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = horizontalProductAdapter
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = verticalProductAdapter
        }
    }

    private fun navigateToProductDetails(productId: Int) {
        val action = MyFeedFragmentDirections.actionMyFeedFragmentToProductDetailsFragment(productId)
        findNavController().navigate(action)
    }

    private fun setupPaginationButtons() {
        paginationBinding.btnPrevious.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updateVisibleProducts()
            }
        }

        paginationBinding.btnNext.setOnClickListener {
            if ((currentPage + 1) * itemsPerPage < totalProducts) {
                currentPage++
                updateVisibleProducts()
            }
        }

        paginationBinding.root.visibility = View.GONE
    }

    private fun updateVisibleProducts() {
        val start = currentPage * itemsPerPage
        val end = minOf(start + itemsPerPage, totalProducts)

        val visibleProducts = viewModel.products.value?.subList(start, end) ?: emptyList()
        verticalProductAdapter.getProducts(visibleProducts)
        updateButtonStates()
    }

    private fun updateButtonStates() {
        paginationBinding.btnPrevious.isEnabled = currentPage > 0
        paginationBinding.btnNext.isEnabled = (currentPage + 1) * itemsPerPage < totalProducts
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            totalProducts = products.size

            if (totalProducts > itemsPerPage) {
                paginationBinding.root.visibility = View.VISIBLE
                updateVisibleProducts()
            } else {
                paginationBinding.root.visibility = View.GONE
                verticalProductAdapter.getProducts(products)
            }

            horizontalProductAdapter.getProducts(products)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}