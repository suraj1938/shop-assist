package com.application.shopassist.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_favourites")
data class Favourites (
    @ColumnInfo(name="product_name")
    var productName:String="",

    @ColumnInfo(name="product_subcategory")
    var productSubCategory:String="")
{
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
}
