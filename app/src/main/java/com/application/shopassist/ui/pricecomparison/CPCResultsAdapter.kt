package com.application.shopassist.ui.pricecomparison

import com.application.shopassist.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.ExpandableLayout
import kotlinx.android.synthetic.main.cpc_expandable_row.view.*
import java.util.*
import kotlin.collections.LinkedHashMap


class CPCResultsAdapter(private val itemsCells: LinkedHashMap<String, LinkedHashMap<String, Double>>) :
    RecyclerView.Adapter<CPCResultsAdapter.ViewHolder>() {

    // Save the expanded row position
    private val expandedPositionSet: HashSet<Int> = HashSet()
    lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cpc_expandable_row, parent, false)
        val vh = ViewHolder(v)
        context = parent.context
        return vh
    }

    // Get item count
    override fun getItemCount(): Int {
        return itemsCells.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Add data to cells
        var storeAmount = ""
        var productAmount = ""

        val stores = itemsCells.keys.toList()
        val amounts = itemsCells.values.toList()

        // Format data
        storeAmount =  "\$" + amounts[position].get("total_amount").toString()

        // Remove total_amount key from amount
        amounts[position].remove("total_amount")

        // Attach the values
        var index = 0
        for (row in amounts[position]) {
            if (index != amounts[position].size - 1) {
                productAmount += row.key + ":\t\t\$" + row.value + "\n"
            }
            else {
                productAmount += row.key + ":\t\t\$" + row.value
            }
            index += 1
        }

        // Set text in the holder
        holder.itemView.store_textview.text = stores[position]
        holder.itemView.prod_amount_textview.text = productAmount
        holder.itemView.amount_textview.text = storeAmount

        // Expand when you click on cell
        holder.itemView.expand_layout.setOnExpandListener(object :
            ExpandableLayout.OnExpandListener {
            override fun onExpand(expanded: Boolean) {
                if (expandedPositionSet.contains(position)) {
                    expandedPositionSet.remove(position)
                } else {
                    expandedPositionSet.add(position)
                }
            }
        })
        // Set the holder of item view in expanded layout
        holder.itemView.expand_layout.setExpand(expandedPositionSet.contains(position))
    }
}