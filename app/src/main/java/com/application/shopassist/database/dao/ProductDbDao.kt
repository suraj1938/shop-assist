package com.application.shopassist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.application.shopassist.database.models.Favourites
import com.application.shopassist.database.models.ProductsDb

@Dao
interface ProductDbDao {
    @Insert
    suspend fun insert(product: ProductsDb):Long
    @Update
    suspend fun update(product: ProductsDb)

    @Query("select * from table_products where productId=:key")
    suspend fun getProductById(key:Long): ProductsDb

    @Query("select * from table_products where product_name=:key")
    suspend fun getProductByName(key:String): ProductsDb

    @Query("select * from table_products")
    suspend fun getAll(): List<ProductsDb>

    @Query("DELETE FROM table_products")
    suspend fun deleteAll ()

    @Query("DELETE FROM table_products where productId=:key")
    suspend fun delete(key:Long)

}