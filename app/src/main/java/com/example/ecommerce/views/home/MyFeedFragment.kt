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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MyFeedFragment : Fragment() {

    private var _binding: FragmentMyFeedBinding? = null
    private val binding get() = _binding!!

    val vm: MyFeedViewModel by viewModels()
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var verticalProductAdapter: ProductAdapter
    private lateinit var horizontalProductAdapter: ProductAdapter
    private var crntSortOptions: Int? = null
    private var lastVerticalFetchTime: Long = 0
    private var lastHorizontalFetchTime: Long = 0
    private val debounceDelay = 500L

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
        horizontalProductAdapter = ProductAdapter { productId ->
            navigateToProductDetails(productId)
        }

        verticalProductAdapter = ProductAdapter { productId ->
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
            popupMenu.menu.findItem(R.id.clear_filter)?.isVisible = crntSortOptions != null
            popupMenu.setOnMenuItemClickListener { item ->
                viewLifecycleOwner.lifecycleScope.launch {
                    when (item.itemId) {
                        R.id.clear_filter -> {
                            vm.setSortOption(null)
                            crntSortOptions = null
                        }
                        else -> {
                            vm.setSortOption(item.itemId)
                            crntSortOptions = item.itemId
                        }
                    }
                    applyFilter(crntSortOptions)
                }
                true
            }
            popupMenu.show()
        }
    }

    fun applyFilter(sortOption: Int?) {
        val verticalProducts = vm.verticalProducts.value
        val horizontalProducts = vm.horizontalProducts.value

        val sortedVerticalProducts = when (sortOption) {
            R.id.sort_price_asc -> verticalProducts.sortedBy { it.price }
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
            val currentTime = System.currentTimeMillis()

            if (!vm.isLoadingVertical.value && scrollY >= childHeight - scrollViewHeight - 200 &&
                currentTime - lastVerticalFetchTime > debounceDelay) {
                lastVerticalFetchTime = currentTime
                vm.fetchAllProducts(MyFeedViewModel.LayoutType.VERTICAL)
            }
        }
    }

    private fun setupHorizontalScrollListener() {
        binding.rvHorizontalProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val scrollOffset = recyclerView.computeHorizontalScrollOffset()
                val scrollExtent = recyclerView.computeHorizontalScrollExtent()
                val scrollRange = recyclerView.computeHorizontalScrollRange()
                val currentTime = System.currentTimeMillis()

                val distanceFromEnd = scrollRange - (scrollOffset + scrollExtent)

                if (!vm.isLoadingHorizontal.value && distanceFromEnd < 200 &&
                    currentTime - lastHorizontalFetchTime > debounceDelay) {
                    lastHorizontalFetchTime = currentTime
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
                        verticalProductAdapter.appendProducts(products)
                    }
                }
                launch {
                    vm.horizontalProducts.collect { products ->
                        horizontalProductAdapter.appendProducts(products)
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
                launch {
                    vm.sortOption.collectLatest { sortOption ->
                        crntSortOptions = sortOption
                        applyFilter(sortOption)
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