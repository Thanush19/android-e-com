package com.example.ecommerce.views.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerce.data.model.Product
import com.example.ecommerce.databinding.OrderDropdownItemBinding

class OrderDropdownAdapter(
    private val onProductClick: (productId: Int) -> Unit
) : RecyclerView.Adapter<OrderDropdownAdapter.OrderViewHolder>() {

    private val products: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderDropdownItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding, onProductClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun getData(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
    }

    class OrderViewHolder(
        private val binding: OrderDropdownItemBinding,
        private val onProductClick: (productId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.title
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
}
