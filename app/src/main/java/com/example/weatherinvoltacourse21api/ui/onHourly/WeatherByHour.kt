package com.example.weatherinvoltacourse21api.ui.onHourly

data class WeatherByHour(
    val time: String,
    val mainTemp: String,
    val feelsLikeTemp: String,
    val idWeather: String,
    val description: String
)
