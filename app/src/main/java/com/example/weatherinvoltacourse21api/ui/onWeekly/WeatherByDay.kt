package com.example.weatherinvoltacourse21api.ui.onWeekly

data class WeatherByDay(
    val time: String,
    val dayTemp: String,
    val nightTemp: String,
    val weatherMain: String,

    val pressure: String,
    val humidity: String,
    val wind_speed: String,
)
