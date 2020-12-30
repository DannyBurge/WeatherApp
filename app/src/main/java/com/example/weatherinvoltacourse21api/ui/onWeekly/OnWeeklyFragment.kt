package com.example.weatherinvoltacourse21api.ui.onWeekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentWeeklyBinding
import com.example.weatherinvoltacourse21api.ui.onHourly.WeatherByHour
import com.example.weatherinvoltacourse21api.ui.onHourly.WeatherByHourListAdapter
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class OnWeeklyFragment : Fragment() {
    var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentWeeklyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weekly, container, false)
        mainActivity = activity as MainActivity?
        binding.root.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val liveData: LiveData<String>? = mainActivity?.getWeeklyInfo()
        liveData?.observe(viewLifecycleOwner, {
            binding.root.visibility = View.VISIBLE
            parseJsonSetText(it)
        })
    }

    private fun parseJsonSetText(result: String) {
        if ((!result.contains("Unable", ignoreCase = true)) and (!result.contains("timeout", ignoreCase = true))) {
            val weatherInfoByDay: MutableList<WeatherByDay> = ArrayList()
            val jsonResult = JSONObject(result)
            val jsonDailyWeather = (jsonResult["daily"] as JSONArray)
            var dailyData: JSONObject
            val sdfDay = java.text.SimpleDateFormat("dd.MM")
            for (dayCounter in 0 until jsonDailyWeather.length()) {
                dailyData = jsonDailyWeather.getJSONObject(dayCounter)
                val dt = java.util.Date(dailyData["dt"].toString().toFloat().toLong() * 1000)

                val tempInfo = JSONObject(dailyData["temp"].toString())
                val dayTemp = tempInfo["day"].toString().toFloat().roundToInt().toString()
                val nightTemp = tempInfo["night"].toString().toFloat().roundToInt().toString()

                val pressure = dailyData["pressure"].toString().toFloat().roundToInt().toString()
                val humidity = dailyData["humidity"].toString().toFloat().roundToInt().toString()
                val windSpeed = dailyData["wind_speed"].toString().toFloat().toString()

                val weatherId = (JSONObject(
                    (dailyData["weather"] as JSONArray).getJSONObject(0).toString()
                )["id"]).toString()
                val weatherDescription = (JSONObject(
                    (dailyData["weather"] as JSONArray).getJSONObject(0).toString()
                )["description"]).toString().capitalize()

                weatherInfoByDay.add(
                    WeatherByDay(
                        "${sdfDay.format(dt)}",
                        "$dayTemp°C",
                        "$nightTemp°C",
                        weatherId,
                        weatherDescription,
                        pressure,
                        humidity,
                        windSpeed
                    )
                )
            }
            binding.dailyWeatherList.adapter =
                WeatherByDayListAdapter(binding.onWeeklyContainer.context, weatherInfoByDay)
        }
    }
}