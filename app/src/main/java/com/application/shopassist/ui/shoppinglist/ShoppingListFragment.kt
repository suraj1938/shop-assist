package com.application.shopassist.ui.shoppinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

import kotlinx.android.synthetic.main.fragment_shoppinglist.*
import android.content.DialogInterface

import android.widget.EditText
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.shopassist.PriceComparison
import com.application.shopassist.R
import com.application.shopassist.database.ShopAssistDatabase
import com.application.shopassist.database.dao.ProductDbDao
import com.application.shopassist.database.dao.ShoppingListDao
import com.application.shopassist.database.dao.ShoppingListWithProductsDao
import com.application.shopassist.database.models.ProductsDb
import com.application.shopassist.database.models.ShoppingList
import com.application.shopassist.database.models.ShoppingListProductCrossRef
import com.application.shopassist.firebase.GlobalProducts
import com.application.shopassist.firebase.models.Product
import com.application.shopassist.firebase.models.Store
import kotlinx.android.synthetic.main.favourite_items.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShoppingListFragment : Fragment(),ShoppingListAdapter.OnItemClickListener,
    ShoppingListFirebaseProductsAdapter.OnItemClickListener,ShoppingListProductsAdapter.OnItemClickListener {


    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var shoppingListItems: MutableList<ShoppingList>
    private  var productsListToAddToDb= mutableListOf<Product>()
    private  var productsListRetrievedFromDb= mutableListOf<ProductsDb>()
    private lateinit var db: ShopAssistDatabase
    private lateinit var shoppingListDao: ShoppingListDao
    private lateinit var productsDbDao: ProductDbDao
    private lateinit var shoppingListWithProductsDao: ShoppingListWithProductsDao
    private var shoppingListName=""

    // Edit item functionality
    override fun onEditItemClicked(listName:String) {
        shoppingListName=listName
        button_create.visibility=View.GONE
        recycler_view_shopping_list.visibility=View.GONE
        button_add_products.visibility=View.VISIBLE
        recycler_view_shopping_list_products_from_db.visibility=View.VISIBLE
        recycler_view_shopping_list_products.visibility=View.GONE
        getProductsOfAListFromDb()
    }

    // Get products from the db
    private fun getProductsOfAListFromDb(){
        val job= Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(this.requireContext())
        productsDbDao=db.productsDao
        shoppingListWithProductsDao=db.shoppingListWithProductsDao
        shoppingListDao=db.shoppingListDao
        scope.launch {
           val shoppingListWithProductsFromDb= shoppingListWithProductsDao.getShoppingListWithProducts()
            for(group in shoppingListWithProductsFromDb){
                if(group.shoppingList.listName == shoppingListName){
                    productsListRetrievedFromDb=group.products.toMutableList()
                }
            }
            recycler_view_shopping_list_products_from_db.visibility=View.VISIBLE
            recycler_view_shopping_list.visibility=View.GONE
            recycler_view_shopping_list_products.visibility=View.GONE
            recycler_view_shopping_list_products_from_db.adapter=ShoppingListProductsAdapter(productsListRetrievedFromDb,this@ShoppingListFragment)
            if(productsListRetrievedFromDb.size==0){
                message_no_products.visibility=View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shoppingListViewModel =
            ViewModelProviders.of(this).get(ShoppingListViewModel::class.java)
        return inflater.inflate(R.layout.fragment_shoppinglist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_create.setOnClickListener {
            showCreateListPopUp()
        }

        // Set on click listeners
        button_add_products.setOnClickListener {
            message_no_products.visibility=View.GONE
            button_add_products.visibility=View.GONE
            recycler_view_shopping_list.visibility=View.GONE
            recycler_view_shopping_list_products_from_db.visibility=View.GONE
            recycler_view_shopping_list_products.visibility=View.VISIBLE

            button_done.visibility=View.VISIBLE
            callFirebaseProductsDisplayView()
        }
        button_done.setOnClickListener {
            if(productsListToAddToDb.size==0){
                val text = "Please select at least one item"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(this.requireContext(), text, duration)
                toast.setGravity(Gravity.TOP,0,160)
                toast .show()
            }else{
                addProductsToDb()
                recycler_view_shopping_list_products.visibility=View.GONE
                button_done.visibility=View.GONE
                recycler_view_shopping_list.visibility=View.GONE
                recycler_view_shopping_list_products_from_db.visibility=View.VISIBLE
                button_add_products.visibility=View.VISIBLE

                //getProductsOfAListFromDb()

            }
        }
        recycler_view_shopping_list.layoutManager= LinearLayoutManager(context)
        recycler_view_shopping_list_products.layoutManager = LinearLayoutManager(context)
        recycler_view_shopping_list_products_from_db.layoutManager = LinearLayoutManager(context)
        getShoppingLists(context!!)
    }

    // Add products to db
    private fun addProductsToDb(){
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(this.requireContext())
        val productIdS= mutableListOf<Long>()
        productsDbDao=db.productsDao
        shoppingListWithProductsDao=db.shoppingListWithProductsDao
        shoppingListDao=db.shoppingListDao
        scope.launch {
            for(product in productsListToAddToDb){
                val id=productsDbDao.insert(ProductsDb(product.productName,product.productSubCategory))
                productIdS.add(id)
            }
            val listId=shoppingListDao.getByName(shoppingListName).shoppingListId
            for(id in productIdS){
                shoppingListWithProductsDao.insert(ShoppingListProductCrossRef(id,listId))
            }
            productsListToAddToDb.clear()
            getProductsOfAListFromDb()
        }

    }

    // To show create list pop up
    private fun showCreateListPopUp(){
        val layoutInflater = LayoutInflater.from(activity!!)
        val promptView = layoutInflater.inflate(R.layout.shopping_list_name,null)
        val alertDialogBuilder = AlertDialog.Builder(activity!!)
        alertDialogBuilder.setView(promptView)
        val input = promptView.findViewById(com.application.shopassist.R.id.userInputShoppingListName) as EditText
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(("OK"), DialogInterface.OnClickListener {
                    dialog, id ->
                createShoppingList(input.text.toString())

            })
            .setNegativeButton("Cancel"
            ) { dialog, id -> dialog.cancel() }

        val alertD = alertDialogBuilder.create()
        alertD.show()
    }

    // To create shopping list
    private fun createShoppingList(name:String){
        db = ShopAssistDatabase.getInstance(this.requireContext())
        shoppingListDao = db.shoppingListDao
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        val listNamesFromDb= mutableListOf<String>()
        scope.launch {
            val shoppingLists=shoppingListDao.getAll()
            for(item in shoppingLists){
                listNamesFromDb.add(item.listName)
            }
            if(name.isEmpty()){
                val text = "Please enter shopping list name"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(context, text, duration)
                toast.setGravity(Gravity.BOTTOM,0,280)
                toast .show()
            }
            else if(listNamesFromDb.contains(name)){

                val text = "A shopping list with the entered name exists"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(context, text, duration)
                toast.setGravity(Gravity.BOTTOM,0,280)
                toast .show()
            }else{
                shoppingListDao.insert(ShoppingList(name))
                shoppingListItems= shoppingListDao.getAll().toMutableList()
                if(shoppingListItems.size==0){
                    message_no_shopping_list.visibility=View.VISIBLE
                }else{
                    message_no_shopping_list.visibility=View.GONE
                }
                recycler_view_shopping_list.adapter=ShoppingListAdapter(this@ShoppingListFragment,shoppingListItems,this@ShoppingListFragment)

            }

        }
    }

    private fun callFirebaseProductsDisplayView(){
        recycler_view_shopping_list_products.visibility=View.VISIBLE
        val localList=GlobalProducts.productList
        val notExistingProducts= mutableListOf<Product>()
        val existingProductNames= mutableListOf<String>()
        for(product in productsListRetrievedFromDb){
            existingProductNames.add(product.productName)
        }
        for(product in localList){
            if(!existingProductNames.contains(product.productName)){
                notExistingProducts.add(product)
            }
        }
        recycler_view_shopping_list_products.adapter =
            ShoppingListFirebaseProductsAdapter( notExistingProducts,this)
    }

    private fun getShoppingLists(context: Context){
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(context)
        shoppingListDao = db.shoppingListDao
        scope.launch {
            shoppingListItems= shoppingListDao.getAll().toMutableList()
            if(shoppingListItems.size==0){
                message_no_shopping_list.visibility=View.VISIBLE
            }
            recycler_view_shopping_list.adapter=ShoppingListAdapter(this@ShoppingListFragment,shoppingListItems,this@ShoppingListFragment)
        }

    }

    override fun onDeleteItemClickedInFirebaseList(product: Product) {
        if(productsListToAddToDb.contains(product)){
            productsListToAddToDb.remove(product)
        }
    }

    override fun onAddItemClickedInFirebaseList(product: Product) {
        Log.i("Shop","clicked ${product.productName}")
        val currentProductNames= mutableListOf<String>()
        for(product1 in productsListToAddToDb){
            currentProductNames.add(product1.productName)
        }
        if(!currentProductNames.contains(product.productName)){
            productsListToAddToDb.add(product)
        }

    }

    override fun onDeleteProductsClicked(prod: ProductsDb, pos: Int) {
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(context!!)
        shoppingListDao = db.shoppingListDao
        productsDbDao=db.productsDao
        shoppingListWithProductsDao=db.shoppingListWithProductsDao
        shoppingListDao=db.shoppingListDao
        scope.launch {
            val listId=shoppingListDao.getByName(shoppingListName).shoppingListId
            shoppingListWithProductsDao.delete(prod.productId,listId)
            getProductsOfAListFromDb()
        }


    }

    override fun onDeleteItemClickedInShoppingListDisplay(listName: String) {
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(context!!)
        shoppingListDao = db.shoppingListDao
        productsDbDao=db.productsDao
        shoppingListWithProductsDao=db.shoppingListWithProductsDao
        shoppingListDao=db.shoppingListDao
        scope.launch {
            val id=shoppingListDao.getByName(listName).shoppingListId
            shoppingListWithProductsDao.deleteCompleteList(id)
            shoppingListDao.delete(id)
            getShoppingLists(context!!)
        }

    }

    override fun onCompareClicked(listName: String) {
        val job = Job()
        val products = mutableListOf<Product>()
        val scope = CoroutineScope(job + Dispatchers.Main)
        db = ShopAssistDatabase.getInstance(context!!)
        shoppingListDao = db.shoppingListDao
        productsDbDao=db.productsDao
        shoppingListWithProductsDao=db.shoppingListWithProductsDao
        shoppingListDao=db.shoppingListDao
        scope.launch {
            val completeList= db.shoppingListWithProductsDao.getShoppingListWithProducts()
            var productsOfList= mutableListOf<ProductsDb>()
            for(group in completeList){
                if(group.shoppingList.listName==listName){
                    productsOfList=group.products.toMutableList()
                }
            }
            for(item in productsOfList){
                products.add(Product(item.productName,"","","",
                    mutableListOf(Store())))
            }
            val cpcResults = PriceComparison.compareMultipleProduct(products)

            // Set global values
            GlobalProducts.cpcResultsLHM = cpcResults
            GlobalProducts.cpcProducts = products
            GlobalProducts.compareComingFromShopppingList=true

            if (cpcResults.isEmpty()) {
                findNavController().navigate(R.id.action_navigation_pricecomparison_to_noProductFound)
            }
            else {
                findNavController().navigate(R.id.action_navigation_pricecomparison_to_CPCResultsFragment)
            }
        }

    }


}