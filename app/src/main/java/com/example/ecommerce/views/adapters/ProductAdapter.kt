package com.example.ecommerce.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerce.R
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.databinding.ItemProductBinding
import com.example.ecommerce.databinding.ItemLoaderBinding
import java.util.Locale

class ProductAdapter(
    private val onProductClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val products: MutableList<Product> = mutableListOf()

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_LOADER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADER -> {
                val binding = ItemLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoaderViewHolder(binding)
            }
            else -> {
                val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProductViewHolder(binding, onProductClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> holder.bind(products[position])
            is LoaderViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateProducts(newProducts: List<Product>) {
        val diffCallback = ProductDiffCallback(products, newProducts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        products.clear()
        products.addAll(newProducts)
        diffResult.dispatchUpdatesTo(this)
    }


    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = binding.root.context.getString(
                R.string.product_price, String.format(Locale.US, "%.2f", product.price)
            )
            binding.ivProductImage.load(product.image) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            binding.root.setOnClickListener { onProductClick(product.id) }
        }
    }

    class LoaderViewHolder(private val binding: ItemLoaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

    class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}