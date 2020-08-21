package com.application.shopassist.ui.pricecomparison

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Price Comparison View Model
class PriceComparisonViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Price comparison Fragment"
    }
    val text: LiveData<String> = _text
}