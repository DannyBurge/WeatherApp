package com.example.weatherinvoltacourse21api.ui.onWeekly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnWeeklyViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is onWeekly Fragment"
    }
    val text: LiveData<String> = _text
}