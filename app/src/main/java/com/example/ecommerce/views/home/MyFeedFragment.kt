package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private var currentProductId = 1
    private val maxProductId = 20
    private var batchSize = 3
    private var isLoading = false
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
        setupScrollListeners()
        setupFilter()
        observeViewModel()
        fetchNextBatch()
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

    private fun setupScrollListeners() {
        binding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
            if (!isLoading && currentProductId <= maxProductId) {
                val layoutManager = binding.rvProducts.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = verticalProductAdapter.itemCount

                if (lastVisibleItemPosition >= totalItemCount - 2) {
                    fetchNextBatch()
                }
            }
        }

        binding.rvHorizontalProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isLoading && currentProductId <= maxProductId) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = horizontalProductAdapter.itemCount

                    if (lastVisibleItemPosition >= totalItemCount - 2) {
                        fetchNextBatch()
                    }
                }
            }
        })
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
        val allProducts = vm.allProducts.value.toMutableList()
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

    private fun fetchNextBatch() {
        if (isLoading || currentProductId > maxProductId) return

        isLoading = true
        _binding?.progressBar?.visibility = View.VISIBLE

        val endId = minOf(currentProductId + batchSize - 1, maxProductId)
        val idsToFetch = (currentProductId..endId).toList()
        vm.fetchProductsByIds(idsToFetch)
    }

    private fun navigateToProductDetails(productId: Int) {
        val action = MyFeedFragmentDirections.actionMyFeedFragmentToProductDetailsFragment(productId)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.products.collectLatest { newProducts ->
                        newProducts?.let {
                            val currentProducts = verticalProductAdapter.getProducts().toMutableList()
                            val filteredNewProducts = it.filter { product ->
                                !currentProducts.any { p -> p.id == product.id }
                            }
                            if (filteredNewProducts.isNotEmpty()) {
                                currentProducts.addAll(filteredNewProducts)
                                val sortedProducts = currentSortOption?.let { sortOption ->
                                    when (sortOption) {
                                        R.id.sort_price_asc -> currentProducts.sortedBy { it.price }
                                        R.id.sort_price_desc -> currentProducts.sortedByDescending { it.price }
                                        R.id.sort_name_asc -> currentProducts.sortedBy { it.title }
                                        R.id.sort_name_desc -> currentProducts.sortedByDescending { it.title }
                                        else -> currentProducts
                                    }
                                } ?: currentProducts
                                verticalProductAdapter.updateProducts(sortedProducts)
                                horizontalProductAdapter.updateProducts(sortedProducts)
                            }
                            isLoading = false
                            _binding?.progressBar?.visibility = View.GONE
                            currentProductId += batchSize
                        }
                    }
                }

                launch {
                    vm.isLoading.collectLatest { loading ->
                        isLoading = loading
                        _binding?.progressBar?.visibility = if (loading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    vm.error.collectLatest { errorMessage ->
                        errorMessage?.let {
                            _binding?.let { binding ->
                                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                                isLoading = false
                                binding.progressBar.visibility = View.GONE
                                currentProductId += batchSize
                            }
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