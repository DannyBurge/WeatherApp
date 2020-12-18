package com.example.weatherinvoltacourse21api.ui.onWeather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnWeatherViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is OnWeather Fragment"
    }
    val text: LiveData<String> = _text
}