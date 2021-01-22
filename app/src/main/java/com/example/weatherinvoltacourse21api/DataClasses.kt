package com.example.weatherinvoltacourse21api

data class City(
    val city: String,
    val country: String,
    val longitude: Float,
    val latitude: Float,
    var isFavourite: Boolean
)

data class CurrentWeatherData(
    val name: String,
    val sys: SysInfo,
    val dt: Long,
    val visibility: Int,
    val wind: WindInfo,
    val weather: List<Weather>,
    val main: MainInfo
)

data class OneCallWeatherData(
    val hourly: List<HourWeatherData>,
    val daily: List<DayWeatherData>
)

data class MainInfo(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Int,
    val humidity: Int,
)

data class WindInfo(
    val speed: Float
)

data class SysInfo(
    val sunrise: Long,
    val sunset: Long,
)

data class HourWeatherData(
    val dt: Long,
    val temp: Float,
    val feels_like: Float,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val windSpeed: Float,
    val weather: List<Weather>
)

data class DayWeatherData(
    val dt: Long,
    val temp: DayTemp,
    val weather: List<Weather>
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String
)

data class DayTemp(
    val day: Float,
    val night: Float
)
