package com.application.shopassist.ui.pricecomparison

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.R
import com.application.shopassist.firebase.models.Product
import kotlinx.android.synthetic.main.product_items.view.*


// Product Catalogue Adapter class
class ProductCatalogueAdapter(private val products: List<Product>,
                              private  val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ProductCatalogueAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_items, parent, false)
        return ViewHolder(
            itemView
        )
    }

    // Get count of products
    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        // Bind products to the holder
        holder.productName.text = product.productName
        holder.bind(products[position],itemClickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView
            get() = itemView.productName

        // Add Products button
        val addButton:ImageView
        get()=itemView.add_btn

        // Delete Products button
        val deleteButton:ImageView
        get()=itemView.delete_btn

        fun bind(product:Product,clickListener: OnItemClickListener)
        {

            // Set on click listener for buttons
            itemView.add_btn.setOnClickListener {
                clickListener.onAddItemClicked(product)
                itemView.add_btn.setImageResource(R.drawable.ic_add_circle_green_24dp)
            }
            itemView.delete_btn.setOnClickListener {
                clickListener.onDeleteItemClicked(product)
                itemView.add_btn.setImageResource(R.drawable.ic_add_circle_black_24dp)
            }
        }
    }

    // Interface for on item click listeners
    interface OnItemClickListener{
        fun onAddItemClicked(product: Product)
        fun onDeleteItemClicked(product: Product)
    }
}