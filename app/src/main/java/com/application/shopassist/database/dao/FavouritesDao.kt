package com.application.shopassist.database.dao

import androidx.room.*
import com.application.shopassist.database.models.Favourites

@Dao
interface FavouritesDao {

    @Insert
    suspend fun insert(fav: Favourites):Long
    @Update
    suspend fun update(fav: Favourites)

    @Query("select * from table_favourites where id=:key")
    suspend fun get(key:Long): Favourites

    @Query("select * from table_favourites")
    suspend fun getAll(): List<Favourites>

    @Query("DELETE FROM table_favourites")
    suspend fun deleteAll ()

    @Query("DELETE FROM table_favourites where id=:key")
    suspend fun delete(key:Long)

    @Query("SELECT * FROM table_favourites order by id DESC LIMIT 1")
    suspend fun getTopFav(): Favourites?


}