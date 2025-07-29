package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.databinding.FragmentMyFeedBinding
import com.example.ecommerce.views.adapters.ProductAdapter
import com.example.ecommerce.views.adapters.ProductAdapter.LayoutType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MyFeedFragment : Fragment() {

    private var _binding: FragmentMyFeedBinding? = null
    private val binding get() = _binding!!

    private val vm: MyFeedViewModel by viewModels()
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var verticalProductAdapter: ProductAdapter
    private lateinit var horizontalProductAdapter: ProductAdapter
    private var currentSortOption: Int? = null

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
        setupFilter()
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

    private fun setupFilter() {
        binding.ivFilter.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.menu_filter, popupMenu.menu)
            popupMenu.menu.findItem(R.id.clear_filter)?.isVisible = currentSortOption != null
            popupMenu.setOnMenuItemClickListener { item ->
                viewLifecycleOwner.lifecycleScope.launch {
                    when (item.itemId) {
                        R.id.clear_filter -> {
                            userPreferencesRepository.clearSortOption()
                            currentSortOption = null
                            applyFilter(null)
                        }
                        else -> {
                            userPreferencesRepository.saveSortOption(item.itemId)
                            currentSortOption = item.itemId
                            applyFilter(item.itemId)
                        }
                    }
                }
                true
            }
            popupMenu.show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userPreferencesRepository.sortOption.collectLatest { sortOption ->
                    currentSortOption = sortOption
                    applyFilter(sortOption)
                }
            }
        }
    }

    private fun applyFilter(sortOption: Int?) {
        val allProducts = vm.allProducts.value
        if (allProducts.isNotEmpty()) {
            val sortedProducts = when (sortOption) {
                R.id.sort_price_asc -> allProducts.sortedBy { it.price }
                R.id.sort_price_desc -> allProducts.sortedByDescending { it.price }
                R.id.sort_name_asc -> allProducts.sortedBy { it.title }
                R.id.sort_name_desc -> allProducts.sortedByDescending { it.title }
                else -> allProducts
            }
            verticalProductAdapter.updateProducts(sortedProducts)
            horizontalProductAdapter.updateProducts(sortedProducts)
            binding.rvProducts.scrollToPosition(0)
            binding.rvHorizontalProducts.scrollToPosition(0)
        } else {
            Toast.makeText(requireContext(), "No products available.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToProductDetails(productId: Int) {
        val action = MyFeedFragmentDirections.actionMyFeedFragmentToProductDetailsFragment(productId)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.allProducts.collect { products ->
                        if (products.isNotEmpty()) {
                            applyFilter(currentSortOption)
                        }
                    }
                }

                launch {
                    vm.isLoading.collect { loading ->
                        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    vm.error.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}