package com.application.shopassist.ui.favourites


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.shopassist.database.ShopAssistDatabase
import com.application.shopassist.database.models.Favourites
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private var job: Job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val _text = MutableLiveData<String>().apply {
        value = "This is favorites Fragment"
    }
    val text: LiveData<String> = _text
    private var favList=MutableLiveData<List<Favourites>>()
    private var _favList=MutableLiveData<List<Favourites>>()

    // To get favourites from database
    init {
        var db = ShopAssistDatabase.getInstance(application.applicationContext)
        var favDao=db.favouritesDao
        scope.launch {
            _favList=MutableLiveData(favDao.getAll())
             favList=_favList
        }
    }


}