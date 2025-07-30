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
        setupVerticalScrollListener()
        setupHorizontalScrollListener()
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
            isNestedScrollingEnabled = false
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = verticalProductAdapter
            isNestedScrollingEnabled = false
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
        val verticalProducts = vm.verticalProducts.value
        val horizontalProducts = vm.horizontalProducts.value

        if (verticalProducts.isNotEmpty() || horizontalProducts.isNotEmpty()) {
            val sortedVerticalProducts = when (sortOption) {
                R.id.sort_price_asc ->  verticalProducts.sortedBy { it.price }
                R.id.sort_price_desc -> verticalProducts.sortedByDescending { it.price }
                R.id.sort_name_asc -> verticalProducts.sortedBy { it.title }
                R.id.sort_name_desc -> verticalProducts.sortedByDescending { it.title }
                else -> verticalProducts
            }
            val sortedHorizontalProducts = when (sortOption) {
                R.id.sort_price_asc -> horizontalProducts.sortedBy { it.price }
                R.id.sort_price_desc -> horizontalProducts.sortedByDescending { it.price }
                R.id.sort_name_asc -> horizontalProducts.sortedBy { it.title }
                R.id.sort_name_desc -> horizontalProducts.sortedByDescending { it.title }
                else -> horizontalProducts
            }
            verticalProductAdapter.updateProducts(sortedVerticalProducts)
            horizontalProductAdapter.updateProducts(sortedHorizontalProducts)
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

    private fun setupVerticalScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            val nestedScrollView = v as NestedScrollView
            val child = nestedScrollView.getChildAt(0)
            val childHeight = child.height
            val scrollViewHeight = nestedScrollView.height

            if (!vm.isLoadingVertical.value && scrollY >= childHeight - scrollViewHeight - 200) {
                vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
            }
        }
    }

    private fun setupHorizontalScrollListener() {
        binding.rvHorizontalProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!vm.isLoadingHorizontal.value && lastVisibleItem >= totalItemCount - 2) {
                    vm.fetchAllProducts(MyFeedViewModel.LayoutType.HORIZONTAL)
                }
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.verticalProducts.collect { products ->
                        applyFilter(currentSortOption)
                    }
                }
                launch {
                    vm.horizontalProducts.collect { products ->
                        applyFilter(currentSortOption)
                    }
                }
                launch {
                    vm.isLoadingVertical.collect { loading ->
                        binding.progressBar.visibility = if (loading || vm.isLoadingHorizontal.value) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    vm.isLoadingHorizontal.collect { loading ->
                        binding.progressBar.visibility = if (loading || vm.isLoadingVertical.value) View.VISIBLE else View.GONE
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
        binding.nestedScrollView.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
        binding.rvHorizontalProducts.clearOnScrollListeners()
        binding.rvProducts.clearOnScrollListeners()
        _binding = null
    }
}