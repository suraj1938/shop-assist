package com.application.shopassist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.application.shopassist.database.dao.FavouritesDao
import com.application.shopassist.database.dao.ProductDbDao
import com.application.shopassist.database.dao.ShoppingListDao
import com.application.shopassist.database.dao.ShoppingListWithProductsDao
import com.application.shopassist.database.models.Favourites
import com.application.shopassist.database.models.ProductsDb
import com.application.shopassist.database.models.ShoppingList
import com.application.shopassist.database.models.ShoppingListProductCrossRef

@Database(entities = [Favourites::class,ProductsDb::class, ShoppingList::class,
    ShoppingListProductCrossRef::class],version =2,exportSchema = false)
abstract class ShopAssistDatabase : RoomDatabase(){
    abstract val favouritesDao: FavouritesDao
    abstract  val shoppingListDao:ShoppingListDao
    abstract  val shoppingListWithProductsDao:ShoppingListWithProductsDao
    abstract  val productsDao:ProductDbDao

    companion object{
        @Volatile
        private var INSTANCE:ShopAssistDatabase?=null

        fun getInstance(context: Context) :ShopAssistDatabase{
            synchronized(this){
                var instance= INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ShopAssistDatabase::class.java,
                        "shop_assist_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE=instance
                }
                return instance
            }


        }

    }
}
