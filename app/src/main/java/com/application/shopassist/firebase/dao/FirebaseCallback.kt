package com.application.shopassist.firebase.dao

import com.application.shopassist.firebase.models.Product

interface FirebaseCallback {
    fun onAsyncDataRetrieval(list:MutableList<Product>)
}
