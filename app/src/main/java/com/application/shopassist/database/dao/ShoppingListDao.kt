package com.application.shopassist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.application.shopassist.database.models.Favourites
import com.application.shopassist.database.models.ShoppingList

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insert(list: ShoppingList)

    @Update
    suspend fun update(list: ShoppingList)

    @Query("select * from table_shopping_list where shoppingListId=:key")
    suspend fun getById(key:Long): ShoppingList

    @Query("select * from table_shopping_list where list_name =:name")
    suspend fun getByName(name:String): ShoppingList

    @Query("select * from table_shopping_list")
    suspend fun getAll(): List<ShoppingList>

    @Query("DELETE FROM table_shopping_list")
    suspend fun deleteAll ()

    @Query("DELETE FROM table_shopping_list where shoppingListId=:key")
    suspend fun delete(key:Long)

}