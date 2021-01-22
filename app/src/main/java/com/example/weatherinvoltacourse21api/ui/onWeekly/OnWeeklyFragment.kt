package com.example.weatherinvoltacourse21api.ui.onWeekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.weatherinvoltacourse21api.DayWeatherData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentWeeklyBinding

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
        val liveData: LiveData<List<DayWeatherData>>? = mainActivity?.getWeeklyInfo()
        liveData?.observe(viewLifecycleOwner, {
            binding.root.visibility = View.VISIBLE
            setText(it)
        })
    }

    private fun setText(dayWeatherInfo: List<DayWeatherData>) {
        val weatherInfoByDay: MutableList<WeatherByDayForAdapter> = ArrayList()
        val sdfDay = java.text.SimpleDateFormat("E, dd.MM")
        for (dayWeather in dayWeatherInfo) {
            weatherInfoByDay.add(
                WeatherByDayForAdapter(
                    sdfDay.format(dayWeather.dt*1000),
                    "${dayWeather.temp.day.toInt()}°C",
                    "${dayWeather.temp.night.toInt()}°C",
                    dayWeather.weather[0].id,
                    dayWeather.weather[0].description,
                )
            )
        }
        binding.dailyWeatherList.adapter =
            WeatherByDayListAdapter(binding.onWeeklyContainer.context, weatherInfoByDay)
    }
}