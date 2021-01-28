package com.example.weatherinvoltacourse21api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherInfoAPI {
    @GET("onecall?exclude=current,minutely,alerts&units=metric&appid=3b5683c272c1dfb381272ff1d030cad3")
    fun getHourlyDailyWeatherInfo(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
    ): Call<OneCallWeatherData?>?


    @GET("weather?units=metric&appid=3b5683c272c1dfb381272ff1d030cad3")
    fun getCurrentWeatherInfo(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
    ): Call<CurrentWeatherData?>?

}