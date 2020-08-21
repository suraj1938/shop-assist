package com.application.shopassist.ui.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.R
import com.application.shopassist.database.models.ProductsDb
import kotlinx.android.synthetic.main.favourite_items.view.*

class ShoppingListProductsAdapter(private val productsFromDb:MutableList<ProductsDb>,
                                  private   val itemClickListener: ShoppingListProductsAdapter.OnItemClickListener)
    : RecyclerView.Adapter<ShoppingListProductsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ShoppingListProductsAdapter.ViewHolder {

        //attach the custom layout to the recycler view
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.favourite_items, viewGroup, false)
        return ShoppingListProductsAdapter.ViewHolder(itemView)
    }
    override fun getItemCount() = productsFromDb.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nameTxt.text = productsFromDb[position].productName
       viewHolder.bind(productsFromDb[position],itemClickListener,position)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTxt: TextView
            get() = itemView.nameTxt

        val deleteImage: ImageView
            get()=itemView.deleteButton

        val compareImage:ImageView
        get()=itemView.compareButton

        fun bind(prod :ProductsDb,clickListener:OnItemClickListener,pos:Int){
            itemView.deleteButton.setOnClickListener {
                clickListener.onDeleteProductsClicked(prod,pos)
            }
            itemView.compareButton.visibility=View.GONE
        }
    }

    interface OnItemClickListener{
        fun onDeleteProductsClicked(prod:ProductsDb,pos:Int)

    }
}