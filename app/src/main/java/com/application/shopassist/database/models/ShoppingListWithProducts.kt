package com.application.shopassist.database.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ShoppingListWithProducts (
    @Embedded var shoppingList: ShoppingList,
    @Relation(
        parentColumn = "shoppingListId",
        entityColumn = "productId",
        associateBy = Junction(ShoppingListProductCrossRef::class)
    )
    val products: List<ProductsDb>
)