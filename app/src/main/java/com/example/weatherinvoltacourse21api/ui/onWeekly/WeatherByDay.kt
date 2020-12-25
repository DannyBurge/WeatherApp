package com.example.weatherinvoltacourse21api.ui.onWeekly

import java.io.FileDescriptor

data class WeatherByDay(
    val day: String,
    val month: String,
    val dayTemp: String,
    val nightTemp: String,
    val idWeather: String,
    val weatherDescription: String,

    val pressure: String,
    val humidity: String,
    val wind_speed: String,
)
