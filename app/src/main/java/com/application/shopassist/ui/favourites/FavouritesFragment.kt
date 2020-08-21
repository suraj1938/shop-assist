package com.application.shopassist.ui.favourites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.shopassist.PriceComparison

import com.application.shopassist.R
import com.application.shopassist.database.ShopAssistDatabase
import com.application.shopassist.database.dao.FavouritesDao
import com.application.shopassist.database.models.Favourites
import com.application.shopassist.firebase.GlobalProducts
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FavouritesFragment : Fragment(),FavouritesAdapter.OnItemClickListener {

    // Function to delete favourites
    override fun onDeleteFavouriteClicked(fav: Favourites,pos :Int) {
        Log.i("fav",fav.productName)
        deleteFavourite(fav,context!!,pos)
    }

    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var favouritesList: MutableList<Favourites>
    private lateinit var favouritesDao: FavouritesDao
    private lateinit var db: ShopAssistDatabase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favouritesViewModel =
            ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_favourites.layoutManager = LinearLayoutManager(context)
        getFavourites(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    // Get favourites from database
    private fun getFavourites(context: Context) {
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(context)
        favouritesDao = db.favouritesDao
        scope.launch {
            favouritesList = favouritesDao.getAll().toMutableList()
            if(favouritesList.size==0){
                message.visibility=View.VISIBLE
            }
            recycler_view_favourites.adapter =
                FavouritesAdapter(this@FavouritesFragment, favouritesList,this@FavouritesFragment)
        }

    }

    // Delete favourites function
    private fun deleteFavourite(fav: Favourites,context: Context,pos:Int){
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(context)
        favouritesDao = db.favouritesDao
        scope.launch {
            favouritesDao.delete(fav.id)
            favouritesList = favouritesDao.getAll().toMutableList()
            if(favouritesList.size==0){
                message.visibility=View.VISIBLE
            }
            recycler_view_favourites.adapter =
                FavouritesAdapter(this@FavouritesFragment, favouritesList,this@FavouritesFragment)

        }

    }

    // Compare it on click of the on-compare button
    override fun onCompareClicked(fav: Favourites) {
        Log.i("Fav",fav.productName)
        val ipcResults = PriceComparison.compareSingleProduct(fav.productName)
        GlobalProducts.ipcResultsLHM = ipcResults
        GlobalProducts.ipcProduct = fav.productName
        GlobalProducts.compareComingFromFavourites=true
        if (ipcResults.isEmpty()) {
            findNavController().navigate(R.id.action_navigation_pricecomparison_to_noProductFound)
        }
        else {
            findNavController().navigate(R.id.action_navigation_pricecomparison_to_IPCResultsFragment)
        }
    }
}



