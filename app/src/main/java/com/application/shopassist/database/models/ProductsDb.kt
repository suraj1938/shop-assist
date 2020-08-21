package com.application.shopassist.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "table_products",indices = arrayOf(Index(value = ["productId"])))
data class ProductsDb (
    @ColumnInfo(name="product_name")
    var productName:String,

    @ColumnInfo(name="product_subcategory")
    var productSubCategory:String

){
    @PrimaryKey(autoGenerate = true)
    var productId:Long=0
}
