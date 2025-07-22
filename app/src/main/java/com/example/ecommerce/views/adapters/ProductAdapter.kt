

package com.example.ecommerce.views.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.databinding.ItemProductBinding
import com.example.ecommerce.databinding.ItemProductHorizontalBinding
import com.example.ecommerce.views.home.ProductDetailsActivity

class ProductAdapter(
    private val layoutType: LayoutType = LayoutType.VERTICAL
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class LayoutType {
        VERTICAL,
        HORIZONTAL
    }

    private val products: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (layoutType) {
            LayoutType.VERTICAL -> {
                val binding = ItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VerticalProductViewHolder(binding)
            }
            LayoutType.HORIZONTAL -> {
                val binding = ItemProductHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HorizontalProductViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VerticalProductViewHolder -> holder.bind(products[position])
            is HorizontalProductViewHolder -> holder.bind(products[position])
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    class VerticalProductViewHolder(
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

    class HorizontalProductViewHolder(
        private val binding: ItemProductHorizontalBinding
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
}