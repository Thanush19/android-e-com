package com.example.ecommerce.views.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.databinding.ItemProductBinding
import com.example.ecommerce.databinding.PaginationControlsBinding
import com.example.ecommerce.views.home.ProductDetailsActivity
import kotlin.math.ceil

class ProductAdapter(
    private val onPageChange: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_PAGINATION = 1
        private const val ITEMS_PER_PAGE = 4
    }

    private val products: MutableList<Product> = mutableListOf()
    private var crntPage = 0
    private var totalPages = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PRODUCT -> {
                val binding = ItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ProductViewHolder(binding)
            }
            else -> {
                val binding = PaginationControlsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PaginationViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                val productPosition = position
                if (productPosition < getCurrentPageProducts().size) {
                    holder.bind(getCurrentPageProducts()[productPosition])
                }
            }
            is PaginationViewHolder -> {
                holder.bind(
                    hasPrev = crntPage > 0,
                    hasNext = crntPage < totalPages - 1,
                    onPrevious = {
                        if (crntPage > 0) {
                            crntPage--
                            onPageChange(crntPage)
                            notifyDataSetChanged()
                        }
                    },
                    onNext = {
                        if (crntPage < totalPages - 1) {
                            crntPage++
                            onPageChange(crntPage)
                            notifyDataSetChanged()
                        }
                    }
                )
            }
        }
    }

    override fun getItemCount(): Int {
        // Products + pagination controls
        return getCurrentPageProducts().size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == getCurrentPageProducts().size) {
            VIEW_TYPE_PAGINATION
        } else {
            VIEW_TYPE_PRODUCT
        }
    }

    private fun getCurrentPageProducts(): List<Product> {
        val start = crntPage * ITEMS_PER_PAGE
        val end = minOf(start + ITEMS_PER_PAGE, products.size)
        return if (start < end) products.subList(start, end) else emptyList()
    }

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        totalPages = ceil(products.size.toDouble() / ITEMS_PER_PAGE).toInt()
        crntPage = 0
        notifyDataSetChanged()
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = "Rs.${product.price}"
            binding.ivProductImage.load(product.image) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                }
                context.startActivity(intent)
            }
        }
    }

    class PaginationViewHolder(
        private val binding: PaginationControlsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            hasPrev: Boolean,
            hasNext: Boolean,
            onPrevious: () -> Unit,
            onNext: () -> Unit
        ) {
            binding.btnPrevious.isEnabled = hasPrev
            binding.btnNext.isEnabled = hasNext

            binding.btnPrevious.setOnClickListener { onPrevious() }
            binding.btnNext.setOnClickListener { onNext() }
        }
    }
}