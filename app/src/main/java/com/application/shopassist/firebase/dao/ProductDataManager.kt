package com.application.shopassist.firebase.dao

import android.util.Log
import com.application.shopassist.firebase.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProductDataManager {

    companion object {
        fun createProducts(productList:List<Product>){
            val nodeRef = FirebaseDatabase.getInstance().getReference("Products")
            productList.forEach {
                it.id = nodeRef.push().key!!
                nodeRef.child(it.productName).setValue(it)
            }
        }


        fun getProducts(fireBaseCallback: FirebaseCallback){
            val nodeRef = FirebaseDatabase.getInstance().getReference("Products")
            var productList=mutableListOf<Product>()
            nodeRef.addValueEventListener(object:ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(data: DataSnapshot) {
                    if(data.exists()){
                        for(product in data.children){
                            val productData=product.getValue(Product::class.java)
                            productList.add(productData!!)

                        }
                        fireBaseCallback.onAsyncDataRetrieval(productList)

                    }
                }
            })
        }

       suspend  fun getIndividualProduct(productName:String,fireBaseCallback: FirebaseCallback){
            val nodeRef = FirebaseDatabase.getInstance().getReference("Products")
            Log.i("Static",productName)
            val childNodeRef=nodeRef.child("$productName")
            var productList=mutableListOf<Product>()
            childNodeRef.addValueEventListener(object:ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(data: DataSnapshot) {
                    if(data.exists()){
                        val productData=data.getValue(Product::class.java)
                        productList.add(productData!!)
                        fireBaseCallback.onAsyncDataRetrieval(productList)

                    }
                }
            })
        }
    }
    /*
     ProductDataManager.getIndividualProduct("Milk",object:FirebaseCallback{
                override fun onAsyncDataRetrieval(list: MutableList<Product>) {
                    Log.i("Mainn",list.size.toString())
                }
            })*/
    
}