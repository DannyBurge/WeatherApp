package com.example.weatherinvoltacourse21api.ui.onHourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.example.weatherinvoltacourse21api.HourWeatherData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentHourlyBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class OnHourlyFragment : Fragment() {
    var mainActivity: MainActivity? = null
    private lateinit var controller: LayoutAnimationController
    private lateinit var binding: FragmentHourlyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hourly, container, false)
        controller = AnimationUtils.loadLayoutAnimation(this.context, R.anim.recycler_animation)
        binding.hourlyWeatherList.layoutAnimation = controller
        mainActivity = activity as MainActivity?
        binding.root.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onResume() {
        controller.animation.duration = 0
        super.onResume()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("ViewCreated")
        // Берем данные из полученного запроса и вешаем слушатель на их изменение
        val liveData: LiveData<List<HourWeatherData>>? = mainActivity?.getHourlyInfo()
        liveData?.observe(viewLifecycleOwner, {
            binding.root.visibility = View.VISIBLE
            // Отображаем анимацию, если в момент обновления мы находимся на текущей вкладке
            if(mainActivity?.binding?.viewPager?.currentItem == 0) {
                controller.animation.duration = 200
            } else controller.animation.duration = 0
            setText(it)
        })
    }

    // Превращаем данные из запроса (которые были преобразованы в объекты дата классов)
    // в список для отображения по шаблону
    private fun setText(hourlyWeatherInfo: List<HourWeatherData>) {
        binding.hourlyWeatherList.scheduleLayoutAnimation()
        val weatherInfoByHour: MutableList<WeatherByHourForAdapter> = ArrayList()

        val sdf = java.text.SimpleDateFormat("HH:mm")
        for (hourWeather in hourlyWeatherInfo) {
            weatherInfoByHour.add(
                WeatherByHourForAdapter(
                    sdf.format(toNearestHour(Date(hourWeather.dt * 1000))),
                    "${hourWeather.temp.toInt()}°C",
                    "${hourWeather.feels_like.toInt()}°C",
                    hourWeather.weather[0].id,
                    hourWeather.weather[0].description
                )
            )
            if (weatherInfoByHour.size == 12) break
        }
        binding.hourlyWeatherList.adapter =
            WeatherByHourListAdapter(binding.hourlyWeatherContainer.context, weatherInfoByHour)
    }

    // Округление времени до ближайшего часа
    private fun toNearestHour(date: Date): Date {
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        if (calendar.get(Calendar.MINUTE) >= 30) calendar.add(Calendar.HOUR, 1)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }
}