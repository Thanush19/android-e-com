package com.example.ecommerce.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.databinding.ItemProductBinding

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val products: MutableList<Product> = mutableListOf()
    private var onProductClick: ((Product) -> Unit)? = null

    fun setOnProductClickListener(listener: (Product) -> Unit) {
        onProductClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.addAll(newProducts)
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: ((Product) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = "Rs${product.price}"
            binding.ivProductImage.load(product.image) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            binding.root.setOnClickListener {
                onProductClick?.invoke(product)
            }
        }
    }
}
