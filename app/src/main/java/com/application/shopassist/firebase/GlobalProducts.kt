package com.application.shopassist.firebase

import android.app.Application
import com.application.shopassist.firebase.models.Product

class GlobalProducts() : Application() {

    companion object {
        var productList=mutableListOf<Product>()
        var ipcResultsLHM=LinkedHashMap<String, Double>()
        var ipcProduct: String= ""
        var cpcResultsLHM=LinkedHashMap<String, LinkedHashMap<String, Double>>()
        var cpcProducts=mutableListOf<Product>()
        var compareComingFromShopppingList:Boolean=false
        var compareComingFromFavourites:Boolean=false
    }

}