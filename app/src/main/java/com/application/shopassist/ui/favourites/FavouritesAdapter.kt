package com.application.shopassist.ui.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.R
import com.application.shopassist.database.models.Favourites
import kotlinx.android.synthetic.main.favourite_items.view.*

// Favourites adapter class
class FavouritesAdapter(
    private val context: FavouritesFragment,
    private var favourites: MutableList<Favourites>, private   val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        //attach the custom layout to the recycler view
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.favourite_items, viewGroup, false)
        return ViewHolder(itemView)
    }

    //returns the number of items in the recycler view
    override fun getItemCount() = favourites.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Binding with view holder
        val fav = favourites[position]
        viewHolder.nameTxt.text = fav.productName
        viewHolder.bind(favourites[position],itemClickListener,position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTxt: TextView
            get() = itemView.nameTxt

        fun bind(fav :Favourites,clickListener:OnItemClickListener,pos:Int){

            // On click listeners for delete and compare
            itemView.deleteButton.setOnClickListener {
                clickListener.onDeleteFavouriteClicked(fav,pos)
            }
            itemView.compareButton.setOnClickListener {
                clickListener.onCompareClicked(fav)
            }
        }
    }

    //  Interface for on-click-listener
    interface OnItemClickListener{
        fun onDeleteFavouriteClicked(fav:Favourites,pos:Int)
        fun onCompareClicked(fav:Favourites)

    }
}