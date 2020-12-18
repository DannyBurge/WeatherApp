package com.example.weatherinvoltacourse21api.ui.onHourly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnHourlyViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is OnHourly Fragment"
    }
    val text: LiveData<String> = _text
}