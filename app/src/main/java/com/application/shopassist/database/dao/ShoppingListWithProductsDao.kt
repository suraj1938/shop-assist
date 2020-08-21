package com.application.shopassist.database.dao

import androidx.room.*
import com.application.shopassist.database.models.ShoppingListProductCrossRef
import com.application.shopassist.database.models.ShoppingListWithProducts

@Dao
interface ShoppingListWithProductsDao {

    @Transaction
    @Query("SELECT * FROM table_shopping_list")
    suspend fun getShoppingListWithProducts():List<ShoppingListWithProducts>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: ShoppingListProductCrossRef)

    @Transaction
    @Query("delete from ShoppingListProductCrossRef where ShoppingListProductCrossRef.productId=:productId and ShoppingListProductCrossRef.shoppingListId=:listId")
    suspend fun delete(productId:Long,listId:Long)

    @Transaction
    @Query("delete from ShoppingListProductCrossRef where ShoppingListProductCrossRef.shoppingListId=:listId ")
    suspend fun deleteCompleteList(listId:Long)


}