package com.application.shopassist.ui.pricecomparison

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Individual Price Comparison View Model
class IPCResultsViewModel(application: Application) : AndroidViewModel(application) {
    private var job: Job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val _text = MutableLiveData<String>().apply {
        value = "This is IPC Results Fragment"
    }
    val text: LiveData<String> = _text
}
