package com.application.shopassist.ui.pricecomparison

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.shopassist.R
import com.application.shopassist.firebase.GlobalProducts
import com.application.shopassist.firebase.models.Product
import com.application.shopassist.ui.pricecomparison.ProductCatalogueAdapter.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_pricecomparison.*
import kotlinx.android.synthetic.main.fragment_shoppinglist.*
import androidx.navigation.fragment.findNavController
import com.application.shopassist.PriceComparison
import kotlinx.android.synthetic.main.fragment_pricecomparison.*

class PriceComparisonFragment : Fragment(), OnItemClickListener {
    override fun onDeleteItemClicked(product: Product) {
        if(productsForCompare.contains(product)){
            productsForCompare.remove(product)
        }
    }

    // Function to add a product
    override fun onAddItemClicked(product: Product) {
        if(!productsForCompare.contains(product)){
            productsForCompare.add(product)
        }
    }

    private lateinit var priceComparisonViewModel: PriceComparisonViewModel
    private var productsForCompare:MutableList<Product> = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        priceComparisonViewModel =
            ViewModelProviders.of(this).get(PriceComparisonViewModel::class.java)
        return inflater.inflate(R.layout.fragment_pricecomparison, container, false)
    }

    private fun compare(){
        if(productsForCompare.size == 0){
            val text = "Please select at least one item"
            val duration = Toast.LENGTH_LONG

            val toast = Toast.makeText(this.requireContext(), text, duration)
            toast.setGravity(Gravity.TOP,0,160)
            toast .show()
        }
        else if (productsForCompare.size == 1) {
            val ipcResults = PriceComparison.compareSingleProduct(productsForCompare[0].productName)

            // Set global values
            GlobalProducts.ipcResultsLHM = ipcResults
            GlobalProducts.ipcProduct = productsForCompare[0].productName

            if (ipcResults.isEmpty()) {
                findNavController().navigate(R.id.action_navigation_pricecomparison_to_noProductFound)
            }
            else {
                findNavController().navigate(R.id.action_navigation_pricecomparison_to_IPCResultsFragment)
            }
        }
        else {
            val cpcResults = PriceComparison.compareMultipleProduct(productsForCompare)

            // Set global values
             GlobalProducts.cpcResultsLHM = cpcResults
             GlobalProducts.cpcProducts = productsForCompare

            if (cpcResults.isEmpty()) {
                findNavController().navigate(R.id.action_navigation_pricecomparison_to_noProductFound)
            }
            else {
                findNavController().navigate(R.id.action_navigation_pricecomparison_to_CPCResultsFragment)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_compare.setOnClickListener {
            compare()
        }

        // Set the adapter of recycler view
        recycler_view_products_list.layoutManager = LinearLayoutManager(context)
        recycler_view_products_list.adapter =
            ProductCatalogueAdapter(GlobalProducts.productList,this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

    }
}