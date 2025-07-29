package com.example.ecommerce.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerce.R
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.databinding.ItemLoaderBinding
import com.example.ecommerce.databinding.ItemProductBinding
import com.example.ecommerce.databinding.ItemProductHorizontalBinding
import java.util.Locale

class ProductAdapter(
    private val layoutType: LayoutType = LayoutType.VERTICAL,
    private val onProductClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class LayoutType {
        VERTICAL,
        HORIZONTAL
    }

    private val products: MutableList<Product> = mutableListOf()
    private var showLoader = false

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_LOADER = 1
    }

    override fun getItemViewType(position: Int): Int {
        val type = if (showLoader && position == products.size) VIEW_TYPE_LOADER else VIEW_TYPE_PRODUCT
        println("DEBUG: getItemViewType position=$position, type=$type")
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        println("DEBUG: Creating ViewHolder for type=$viewType")
        return when (viewType) {
            VIEW_TYPE_LOADER -> {
                val binding = ItemLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoaderViewHolder(binding)
            }
            else -> when (layoutType) {
                LayoutType.VERTICAL -> {
                    val binding = ItemProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    VerticalProductViewHolder(binding, onProductClick)
                }
                LayoutType.HORIZONTAL -> {
                    val binding = ItemProductHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    HorizontalProductViewHolder(binding, onProductClick)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        println("DEBUG: Binding ViewHolder at position=$position")
        if (holder is LoaderViewHolder) {
            holder.bind()
        } else {
            when (holder) {
                is VerticalProductViewHolder -> holder.bind(products[position])
                is HorizontalProductViewHolder -> holder.bind(products[position])
            }
        }
    }

    override fun getItemCount(): Int {
        val count = products.size + if (showLoader) 1 else 0
        println("DEBUG: getItemCount returning $count")
        return count
    }

    fun updateProducts(newProducts: List<Product>) {
        println("DEBUG: Updating products, new size=${newProducts.size}")
        val diffCallback = ProductDiffCallback(products, newProducts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        products.clear()
        products.addAll(newProducts)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setShowLoader(show: Boolean) {
        if (showLoader == show) return
        println("DEBUG: setShowLoader called with show=$show")
        showLoader = show
        if (show) {
            notifyItemInserted(products.size)
        } else {
            notifyItemRemoved(products.size)
        }
    }


    class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    class VerticalProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = binding.root.context.getString(
                R.string.product_price,
                String.format(Locale.US, "%.2f", product.price)
            )
            binding.ivProductImage.load(product.image) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            binding.root.setOnClickListener {
                onProductClick(product.id)
            }
        }
    }

    class HorizontalProductViewHolder(
        private val binding: ItemProductHorizontalBinding,
        private val onProductClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = binding.root.context.getString(
                R.string.product_price,
                String.format(Locale.US, "%.2f", product.price)
            )
            binding.ivProductImage.load(product.image) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            binding.root.setOnClickListener {
                onProductClick(product.id)
            }
        }
    }

    class LoaderViewHolder(private val binding: ItemLoaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            // Loader is shown by default, nothing to bind
        }
    }
}