package com.example.weatherinvoltacourse21api.ui.onHourly

data class WeatherByHourForAdapter(
    val time: String,
    val mainTemp: String,
    val feelsLikeTemp: String,
    val idWeather: Int,
    val description: String
)
