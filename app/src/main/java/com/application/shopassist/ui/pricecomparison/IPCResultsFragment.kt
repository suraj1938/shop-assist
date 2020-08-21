package com.application.shopassist.ui.pricecomparison

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.shopassist.PriceComparison
import com.application.shopassist.R
import com.application.shopassist.database.ShopAssistDatabase
import com.application.shopassist.database.models.Favourites
import com.application.shopassist.firebase.GlobalProducts
import kotlinx.android.synthetic.main.fragment_pricecomparison.*
import kotlinx.android.synthetic.main.i_p_c_results_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


// Fragment class for Integrated Price Comparison
class IPCResultsFragment : Fragment() {
    companion object {
        fun newInstance() = IPCResultsFragment()
    }

    private lateinit var ipcViewModel: IPCResultsViewModel
    private lateinit var storePriceMap: LinkedHashMap<String, Double>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ipcViewModel =  ViewModelProviders.of(this).get(IPCResultsViewModel::class.java)
        return inflater.inflate(R.layout.i_p_c_results_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Updating actionbar name
        (activity as AppCompatActivity).supportActionBar?.title = "Price Comparison Results"
        // Hide back button
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set text of text view
        textView2.text = GlobalProducts.ipcProduct

        ipcRecyclerView.layoutManager = LinearLayoutManager(context)
        getIPCResults(context!!)
        if(GlobalProducts.compareComingFromFavourites){
            button2.visibility=View.GONE
        }
        GlobalProducts.compareComingFromFavourites=false

        // Add to favourites listener:
        button2.setOnClickListener {
            if (textView2.text.toString().isNotEmpty()) {
                var job: Job = Job()
                val scope = CoroutineScope(job + Dispatchers.Main)

                // Get product details
                val allProductNames: List<String> = GlobalProducts.productList.map { it.productName }
                val prodIndex = allProductNames.indexOf(textView2.text.toString())
                val productName = GlobalProducts.productList[prodIndex].productName
                val productSubCat = GlobalProducts.productList[prodIndex].productSubCategory

                // Get database instance
                var db = ShopAssistDatabase.getInstance(context!!)
                var favDao= db.favouritesDao

                // Checking favourites
                scope.launch {
                   var favList= favDao.getAll()
                    var favPresent:Boolean=false
                    for(fav in favList){
                        if(fav.productName==productName){
                            favPresent=true
                        }
                    }
                    if(favPresent){
                        val text = "Item already present in the favourites"
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(context!!, text, duration)
                        toast.setGravity(Gravity.BOTTOM,0,280)
                        toast .show()
                    }else{
                        favDao.insert(Favourites(productName, productSubCat))
                        val text = "Successfully added to the favourites"
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(context!!, text, duration)
                        toast.setGravity(Gravity.BOTTOM,0,280)
                        toast .show()
                    }

                }

            }
        }
    }

    // Function to get Individual price comparison results
    private fun getIPCResults(context: Context) {
        val job: Job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)

        // Obtain results and set the adapter
        scope.launch {
            storePriceMap = GlobalProducts.ipcResultsLHM
            ipcRecyclerView.adapter =
                IPCResultsAdapter(this@IPCResultsFragment, storePriceMap)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
