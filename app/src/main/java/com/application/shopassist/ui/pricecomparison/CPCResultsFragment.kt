package com.application.shopassist.ui.pricecomparison

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.shopassist.R
import com.application.shopassist.database.ShopAssistDatabase
import com.application.shopassist.database.models.ProductsDb
import com.application.shopassist.database.models.ShoppingList
import com.application.shopassist.database.models.ShoppingListProductCrossRef
import com.application.shopassist.firebase.GlobalProducts
import kotlinx.android.synthetic.main.cpc_results_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CPCResultsFragment : Fragment() {

    private lateinit var adapter: CPCResultsAdapter

    companion object {
        fun newInstance() = CPCResultsFragment()
    }

    // Initialize view model
    private lateinit var viewModel: CpcResultsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cpc_results_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Updating actionbar name
        (activity as AppCompatActivity).supportActionBar?.title = "Price Comparison Results"
        // Hide back button
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set the adapter for recycler view
        adapter = CPCResultsAdapter(GlobalProducts.cpcResultsLHM)
        val llm = LinearLayoutManager(context!!)

        cpcRecyclerView.hasFixedSize()
        cpcRecyclerView.layoutManager = llm
        cpcRecyclerView.adapter = adapter

        // Setting visibility after one click
        if(GlobalProducts.compareComingFromShopppingList){
            button8.visibility=View.GONE
        }
        GlobalProducts.compareComingFromShopppingList=false

        // Save Shopping List listener:
        button8.setOnClickListener {

            if (GlobalProducts.cpcProducts.isNotEmpty())
            {
                var job: Job = Job()
                val scope = CoroutineScope(job + Dispatchers.Main)

                val db = ShopAssistDatabase.getInstance(this.requireContext())
                val shoppingListDao = db.shoppingListDao
                var productIdS= mutableListOf<Long>()

                val productsDbDao= db.productsDao

                val shoppingListWithProductsDao= db.shoppingListWithProductsDao

                scope.launch {
                    // First step is to insert ShoppingList
                    val s = SimpleDateFormat("dd-MM-yy_hhmmss",Locale.CANADA)
                    val stringDate: String = s.format(Date())
                    val shoppingListName: String = "List_$stringDate"

                    shoppingListDao.insert(ShoppingList(shoppingListName))

                    // Second step is to insert products in the database
                    for (product in GlobalProducts.cpcProducts) {
                        var id = productsDbDao.insert(
                            ProductsDb(
                                product.productName,
                                product.productSubCategory
                            )
                        )
                        productIdS.add(id)
                    }

                    // Third step is to add to ShoppingListProductCrossRef
                    var listId = shoppingListDao.getByName(shoppingListName).shoppingListId
                    for (id in productIdS) {
                        shoppingListWithProductsDao.insert(ShoppingListProductCrossRef(id, listId))
                    }
                    // CLear the GlobalList after inserting to the database
                    GlobalProducts.cpcProducts.clear()
                }

                val text = "Successfully saved the shopping list"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(context, text, duration)
                toast.setGravity(Gravity.BOTTOM,0,280)
                toast .show()
            }
            else {
                val text = "Shopping list already saved"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(context, text, duration)
                toast.setGravity(Gravity.BOTTOM,0,280)
            }
        }
        // Set on click listener
        button9.setOnClickListener {
            val toast = Toast.makeText(context!!, "Feature coming soon..", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,280)
            toast.show()
        }

        // Set on click listener
        button7.setOnClickListener {
            val toast = Toast.makeText(context!!, "Feature coming soon..", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,280)
            toast.show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CpcResultsViewModel::class.java)
    }

}
