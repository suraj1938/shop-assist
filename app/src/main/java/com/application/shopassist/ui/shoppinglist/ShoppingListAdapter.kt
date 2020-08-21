package com.application.shopassist.ui.shoppinglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.R
import com.application.shopassist.database.models.ShoppingList
import kotlinx.android.synthetic.main.shopping_list_items.view.*

class ShoppingListAdapter(private val context: ShoppingListFragment,
                          private val listNames: List<ShoppingList>,
                          private   val itemClickListener: OnItemClickListener ) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_list_items, parent, false)
        return ViewHolder(itemView)
    }

    // Get item size
    override fun getItemCount() = listNames.size

    override fun onBindViewHolder(holder: ShoppingListAdapter.ViewHolder, position: Int) {

        // Set the holder
        holder.listName.text = listNames[position].listName
        holder.bind(itemClickListener,listNames[position].listName)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listName: TextView
        get()=itemView.listName

        // Bind the click listeners
        fun bind(clickListener: OnItemClickListener,listName:String){
            itemView.edit_btn.setOnClickListener {
                clickListener.onEditItemClicked(listName)
            }
            itemView.delete_btn.setOnClickListener {
                clickListener.onDeleteItemClickedInShoppingListDisplay(listName)
            }
            itemView.compare_btn.setOnClickListener {
                clickListener.onCompareClicked(listName)
            }
        }

    }

    // Interface for on item click listener
    interface OnItemClickListener{
        fun onEditItemClicked(listName:String)
        fun onDeleteItemClickedInShoppingListDisplay(listName: String)
        fun onCompareClicked(listName: String)
    }
}