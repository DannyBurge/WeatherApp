package com.example.weatherinvoltacourse21api.ui.onHourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.weatherinvoltacourse21api.HourWeatherData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentHourlyBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

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
        binding.root.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Hour View onViewCreated")
        val liveData: LiveData<List<HourWeatherData>>? = mainActivity?.getHourlyInfo()
        liveData?.observe(viewLifecycleOwner, {
            binding.root.visibility = View.VISIBLE
            setText(it)
        })
    }

    private fun setText(hourlyWeatherInfo: List<HourWeatherData>) {
        val weatherInfoByHour: MutableList<WeatherByHourForAdapter> = ArrayList()

        val sdf = java.text.SimpleDateFormat("HH:mm")
        for (hourWeather in hourlyWeatherInfo) {
            weatherInfoByHour.add(
                WeatherByHourForAdapter(
                    sdf.format(toNearestHour(Date(hourWeather.dt*1000))),
                    "${hourWeather.temp.toInt()}°C",
                    "${hourWeather.feels_like.toInt()}°C",
                    hourWeather.weather[0].id,
                    hourWeather.weather[0].description
                )
            )
        }
        binding.hourlyWeatherList.adapter =
            WeatherByHourListAdapter(binding.hourlyWeatherContainer.context, weatherInfoByHour)
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