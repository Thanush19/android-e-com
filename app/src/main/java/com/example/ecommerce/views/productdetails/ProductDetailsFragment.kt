package com.example.ecommerce.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.ecommerce.R
import com.example.ecommerce.data.db.entity.Order
import com.example.ecommerce.data.preferences.UserPreferencesRepository
import com.example.ecommerce.data.repository.OrdersRepository
import com.example.ecommerce.data.repository.ProductRepository
import com.example.ecommerce.databinding.FragmentProductDetailsBinding
import com.example.ecommerce.views.productdetails.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var ordersRepository: OrdersRepository

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var productDetailsViewModel: ProductDetailsViewModel

    private val args: ProductDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = args.productId

        lifecycleScope.launch {
            val product = productDetailsViewModel.getProductById(productId)
            if (product != null) {
                binding.tvProductTitle.text = product.title
                binding.tvProductPrice.text = getString(R.string.product_price, product.price)
                binding.tvProductDescription.text = product.description
                binding.tvProductCategory.text = getString(R.string.product_category, product.category)
                binding.tvProductRating.text = getString(
                    R.string.product_rating,
                    product.rating.rate,
                    product.rating.count
                )
                binding.ivProductImage.load(product.image) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_product), Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }

        binding.btnBuyNow.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val productId = args.productId
        if (productId == -1) {
            Toast.makeText(requireContext(), getString(R.string.invalid_product), Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_purchase_title))
            .setMessage(getString(R.string.confirm_purchase_message))
            .setPositiveButton(getString(R.string.buy_now)) { dialog, _ ->
                dialog.dismiss()
                processOrder(productId)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)

        builder.create().show()
    }

    private fun processOrder(productId: Int) {
        lifecycleScope.launch {
            val userId = userPreferencesRepository.userId.first()

            if (userId == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_to_place_order),
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val order = Order(
                userId = userId,
                productIds = listOf(productId)
            )

            try {
                val orderId = ordersRepository.placeOrder(order)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.order_placed_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.failed_to_place_order, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}