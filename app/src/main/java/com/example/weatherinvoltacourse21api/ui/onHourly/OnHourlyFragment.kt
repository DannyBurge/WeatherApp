package com.example.weatherinvoltacourse21api.ui.onHourly

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherinvoltacourse21api.CitiesRecyclerAdapter
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentHourlyBinding
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
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
            val sdf = java.text.SimpleDateFormat("HH:mm")
            for (hourCounter in 0 until jsonHourlyWeather.length()) {
                hourlyData = jsonHourlyWeather.getJSONObject(hourCounter)
                val dt = java.util.Date(hourlyData["dt"].toString().toFloat().toLong() * 1000)

                val tempMain = hourlyData["temp"].toString().toFloat().roundToInt()
                val weatherInfo = (JSONObject(
                    (hourlyData["weather"] as JSONArray).getJSONObject(0).toString()
                )["description"]).toString().capitalize()

                weatherInfoByHour.add(WeatherByHour(sdf.format(dt), "$tempMain°C", weatherInfo))
            }
            binding.hourlyWeatherList.adapter =
                WeatherByHourListAdapter(binding.hourlyWeatherContainer.context, weatherInfoByHour)
        }
    }
}