package com.application.shopassist.firebase.models

data class Product(
    var productName:String="",
    var productSubCategory:String="",
    var barcode:String="",
    var brand:String="",var storeList :MutableList<Store>)
{
    var id:String=""

    constructor() :this("", "",
        "","", mutableListOf(Store("",0.00,"","")))


}