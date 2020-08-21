package com.application.shopassist

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.application.shopassist.database.models.Favourites
import com.application.shopassist.database.dao.FavouritesDao
import com.application.shopassist.database.ShopAssistDatabase
import com.application.shopassist.database.dao.ProductDbDao
import com.application.shopassist.database.dao.ShoppingListDao
import com.application.shopassist.database.dao.ShoppingListWithProductsDao
import com.application.shopassist.database.models.ProductsDb
import com.application.shopassist.database.models.ShoppingList
import com.application.shopassist.database.models.ShoppingListProductCrossRef
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShopAssistDatabaseTest {
    //
    private lateinit var favouritesDao: FavouritesDao
    private lateinit var productDbDao: ProductDbDao
    private lateinit var shoppingListDao: ShoppingListDao
    private lateinit var shoppingListWithProductsDao: ShoppingListWithProductsDao
    private lateinit var db: ShopAssistDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ShopAssistDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        favouritesDao = db.favouritesDao
        shoppingListDao=db.shoppingListDao
        shoppingListWithProductsDao=db.shoppingListWithProductsDao
        productDbDao=db.productsDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetFav() {
        val fav = Favourites()
        fav.productName="Milk"
        favouritesDao.insert(fav)
        val fav2 = favouritesDao.getTopFav()
        val fav3=favouritesDao.getAll()
        Assert.assertEquals(fav2?.productName, "Milk")
    }
    @Test
    @Throws(Exception::class)
    fun insertAndGetShoppingList() {
        val list = ShoppingList()
        list.listName="List 1"
        shoppingListDao.insert(list)
        val fav2 = shoppingListDao.getAll()
        Assert.assertEquals(fav2.size, 1)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetProducts() {
        val product = ProductsDb()
        product.productName="P1"
        productDbDao.insert(product)
        val fav2 = productDbDao.getAll()
        Assert.assertEquals(fav2.size, 1)
    }
    
}
