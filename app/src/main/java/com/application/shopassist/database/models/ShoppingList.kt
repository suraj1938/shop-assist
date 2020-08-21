package com.application.shopassist.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_shopping_list")
data class ShoppingList (@ColumnInfo(name="list_name")
                         var listName:String){
    @PrimaryKey(autoGenerate = true)
    var shoppingListId:Long=0
}
