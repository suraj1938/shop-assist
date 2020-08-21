package com.application.shopassist

import android.util.Log
import com.application.shopassist.firebase.GlobalProducts
import com.application.shopassist.firebase.dao.FirebaseCallback
import com.application.shopassist.firebase.dao.ProductDataManager
import com.application.shopassist.firebase.models.Product
import com.application.shopassist.firebase.models.Store
import kotlin.math.round

class PriceComparison {

    companion object {

        // function to round the decimals to 2 places
        private fun roundToTwo(x: Double) = round(x * 100) / 100

        // function to get price of products at each store
        private fun getProductPrice(productName: String, allProductNames: List<String>): MutableList<Store> {
            val prodIndex = allProductNames.indexOf(productName)
            return GlobalProducts.productList[prodIndex].storeList
        }

        // function to compare single product and return the amount of product at each store
        fun compareSingleProduct(prodName: String): LinkedHashMap<String, Double> {

            // Used LinkedHashMap (LHM) instead of HashMap because LHM maintains the order
            val storePriceMap = LinkedHashMap<String, Double>()

            // Get list of product names from the products object. Putting in list to optimize the search

            val allProductNames: List<String> = GlobalProducts.productList.map { it.productName }
            Log.i("comp",prodName)
            Log.i("comp",allProductNames.toString())
            Log.i("comp",allProductNames.contains(prodName).toString())
            if (prodName in allProductNames) {
                // get all prices of a product at different stores
                val allStores = getProductPrice(prodName, allProductNames)

                // Sort by cheapest
                allStores.sortBy { it.productPrice }

                for (store in allStores) {
                    storePriceMap[store.name] = roundToTwo(store.productPrice)
                }
            }

            return storePriceMap
        }

        // function to sort the LinkedHashMap by total amount in ascending order
        private fun sortNestedLHM(lhm: LinkedHashMap<String, LinkedHashMap<String, Double>>): LinkedHashMap<String, LinkedHashMap<String, Double>> {
            val sortedLHM = LinkedHashMap<String, LinkedHashMap<String, Double>>()
            val totalAmounts = arrayListOf<Double>()

            for (row in lhm) {
                totalAmounts.add(row.value?.get("total_amount")!!)
            }

            // Sort array of amounts
            totalAmounts.sort()

            var storeKey: String = ""

            // Re-order the lhm
            for (amt in totalAmounts)
            {
                for (row in lhm)
                {
                    if (row.value.get("total_amount") == amt)
                    {
                        sortedLHM[row.key] = row.value
                        storeKey = row.key
                        break
                    }
                }
                // Delete the entry with stored store key from lhm
                lhm.remove(storeKey)
            }

            return sortedLHM
        }

        // function to compare multiple products and return the amount of each product at each store
        fun compareMultipleProduct(prodList: MutableList<Product>): LinkedHashMap<String, LinkedHashMap<String, Double>> {
            // Get list of product names from the products object. Putting in list to optimize the search
            val allProductNames: List<String> = GlobalProducts.productList.map { it.productName }

            // Used LinkedHashMap (LHM) of LinkedHashMap instead of HashMap because LHM maintains the order
            // It will contain each products price as well as the total price at each store
            var pricesByStores = LinkedHashMap<String, LinkedHashMap<String, Double>>()

            // Get list of product names from the products object. Putting in list to optimize the search
            val compareProductNames: List<String> = prodList.map { it.productName }

            // Start comparison
            for (prod in compareProductNames) {
                val allProductStores = getProductPrice(prod, allProductNames)

                for (store in allProductStores) {

                    if (pricesByStores.containsKey(store.name)) {
                        val tempVar = pricesByStores[store.name]?.get("total_amount")

                        if (tempVar?.isNaN() == false)
                        {
                            pricesByStores[store.name]?.put("total_amount", roundToTwo(tempVar + store.productPrice))
                        }
                    }
                    else {
                        // Adding empty LHM for new store key
                        pricesByStores[store.name] = LinkedHashMap<String, Double>()

                        // Add the total amount key with value as product's price
                        pricesByStores[store.name]?.put("total_amount", roundToTwo(store.productPrice))
                    }

                    // Add the product price in nested LHM
                    pricesByStores[store.name]?.put(prod, roundToTwo(store.productPrice))
                }
            }

            // Sort by cheapest
            pricesByStores = sortNestedLHM(pricesByStores)

            return pricesByStores
        }

    }
}