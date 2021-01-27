package com.example.weatherinvoltacourse21api.ui.onWeekly

import java.io.FileDescriptor

data class WeatherByDay(
    val date: String,
    val dayTemp: String,
    val nightTemp: String,
    val idWeather: Int,
    val weatherDescription: String,

    val pressure: String,
    val humidity: String,
    val wind_speed: String,
)