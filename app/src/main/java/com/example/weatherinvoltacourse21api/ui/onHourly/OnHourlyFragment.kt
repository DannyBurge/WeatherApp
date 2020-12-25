package com.example.weatherinvoltacourse21api.ui.onHourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentHourlyBinding
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class OnHourlyFragment : Fragment() {
    var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentHourlyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hourly, container, false)
        mainActivity = activity as MainActivity?
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Hour View onViewCreated")
        val liveData: LiveData<String>? = mainActivity?.getHourlyInfo()
        liveData?.observe(viewLifecycleOwner, {
            binding.root.visibility = View.VISIBLE
            parseJsonSetText(it)
        })

//        val isNewRequest: LiveData<Boolean>? = mainActivity?.isNewRequest()
//        isNewRequest?.observe(viewLifecycleOwner, {
//            doAnimation = it
//        })
    }

    private fun parseJsonSetText(result: String) {
        if (!result.contains("Unable", ignoreCase = true)) {
            val weatherInfoByHour: MutableList<WeatherByHour> = ArrayList()
            val jsonResult = JSONObject(result)
            val jsonHourlyWeather = (jsonResult["hourly"] as JSONArray)
            var hourlyData: JSONObject
            val sdf = java.text.SimpleDateFormat("H")
            for (hourCounter in 0..12) {
                hourlyData = jsonHourlyWeather.getJSONObject(hourCounter)
                val dt = Date((hourlyData["dt"].toString().toFloat() * 1000).toLong())

                val tempMain = hourlyData["temp"].toString().toFloat().roundToInt()
                val feelsLikeMain = hourlyData["feels_like"].toString().toFloat().roundToInt()
                val weatherId = (JSONObject(
                    (hourlyData["weather"] as JSONArray).getJSONObject(0).toString()
                )["id"]).toString()
                val weatherDescription = (JSONObject(
                    (hourlyData["weather"] as JSONArray).getJSONObject(0).toString()
                )["description"]).toString().capitalize(Locale.ROOT)

                weatherInfoByHour.add(WeatherByHour(sdf.format(toNearestHour(dt)), "$tempMain°C", "$feelsLikeMain°C", weatherId, weatherDescription))
            }
            binding.hourlyWeatherList.adapter =
                WeatherByHourListAdapter(binding.hourlyWeatherContainer.context, weatherInfoByHour)
        }
    }

    private fun toNearestHour(date: Date): Date {
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        if (calendar.get(Calendar.MINUTE) >= 30) calendar.add(Calendar.HOUR, 1)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }
}