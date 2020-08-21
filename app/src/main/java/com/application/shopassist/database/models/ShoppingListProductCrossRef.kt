package com.application.shopassist.database.models

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["shoppingListId", "productId"],indices =
arrayOf(Index(value = ["productId","shoppingListId"])))
data class ShoppingListProductCrossRef (
    var productId:Long,
    val shoppingListId:Long
)