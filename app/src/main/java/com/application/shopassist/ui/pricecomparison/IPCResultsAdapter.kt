package com.application.shopassist.ui.pricecomparison

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.R
import kotlinx.android.synthetic.main.ipc_row.view.*

// Adapter for Individual Price Comparison
class IPCResultsAdapter (
    private val context: IPCResultsFragment,
    private var storePriceMap: LinkedHashMap<String, Double>
) : RecyclerView.Adapter<IPCResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        // Attaching the ipc_row layout to the recycler view
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ipc_row, viewGroup, false)
        return ViewHolder(itemView)
    }

    //returns the number of items in the recycler view
    override fun getItemCount() = storePriceMap.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val stores = storePriceMap.keys.toList()
        val amounts = storePriceMap.values.toList()

        // Set the view holder
        viewHolder.store.text = stores[position]
        val amount: String = "\$" + amounts[position].toString()
        viewHolder.price.text = amount
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val store: TextView
            get() = itemView.textView5

        val price: TextView
            get() = itemView.textView6
    }

}