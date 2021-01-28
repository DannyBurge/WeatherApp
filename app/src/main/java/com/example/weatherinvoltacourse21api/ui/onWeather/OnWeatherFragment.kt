package com.example.weatherinvoltacourse21api.ui.onWeather

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.example.weatherinvoltacourse21api.CurrentWeatherData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.RecyclerViewActivity
import com.example.weatherinvoltacourse21api.databinding.FragmentWeatherBinding
import kotlin.math.abs


class OnWeatherFragment : Fragment(){

    var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentWeatherBinding
    private var doAnimation: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false)
        mainActivity = activity as MainActivity?
        binding.root.visibility = View.INVISIBLE


        binding.buttonLocationCity.setOnClickListener {
            val intent = Intent(mainActivity, RecyclerViewActivity::class.java)
            startActivityForResult(intent, 1)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val liveData: LiveData<CurrentWeatherData>? = mainActivity?.getCurrentInfo()
        liveData?.observe(viewLifecycleOwner) {
            binding.root.visibility = View.VISIBLE
            setText(it)
        }

        val isNewRequest: LiveData<Boolean>? = mainActivity?.isNewRequest()
        isNewRequest?.observe(viewLifecycleOwner) {
            doAnimation = it
        }
    }

    override fun onResume() {
        super.onResume()
        doAnimation = false
    }

    @SuppressLint("SetTextI18n")
    fun setText(currentWeatherInfo: CurrentWeatherData) {
            val sdf = java.text.SimpleDateFormat("HH:mm")

            binding.cityName.text = currentWeatherInfo.name

            binding.tempMain.text = "${currentWeatherInfo.main.temp.toInt()}°C"
            binding.tempMax.text = "${currentWeatherInfo.main.temp_max.toInt()}°C"
            binding.tempMin.text = "${currentWeatherInfo.main.temp_min.toInt()}°C"
            binding.tempFeelsLike.text = "Feels Like: ${currentWeatherInfo.main.feels_like.toInt()}°C"

            binding.mainWeather.text = currentWeatherInfo.weather[0].description
            binding.sunRise.text = sdf.format(java.util.Date(currentWeatherInfo.sys.sunrise * 1000))
            binding.sunSet.text = sdf.format(java.util.Date(currentWeatherInfo.sys.sunset * 1000))
            binding.windInfo.text = "${currentWeatherInfo.wind.speed} m/s"
            binding.humidityInfo.text = "${currentWeatherInfo.main.humidity} %"
            binding.visibilityInfo.text = "${(currentWeatherInfo.visibility/1000).toInt()} km"
            binding.pressureInfo.text = "${currentWeatherInfo.main.pressure} mmHg"

            val barMain: ProgressBar
            val barFeelsLike: ProgressBar

            if (currentWeatherInfo.main.temp < 0) {
                barMain = binding.tempCold
                barFeelsLike = binding.tempColdFeelsLikeBar
                binding.tempHot.visibility = View.INVISIBLE
                binding.tempFeelsLikeBar.visibility = View.INVISIBLE
                binding.tempCold.visibility = View.VISIBLE
                binding.tempColdFeelsLikeBar.visibility = View.VISIBLE
            } else {
                barMain = binding.tempHot
                barFeelsLike = binding.tempFeelsLikeBar
                binding.tempHot.visibility = View.VISIBLE
                binding.tempFeelsLikeBar.visibility = View.VISIBLE
                binding.tempCold.visibility = View.INVISIBLE
                binding.tempColdFeelsLikeBar.visibility = View.INVISIBLE
            }

            if (doAnimation) {
                //Первая шкала "Температура"
                var animation = ObjectAnimator.ofInt(
                    barMain,
                    "progress",
                    0,
                    abs(currentWeatherInfo.main.temp.toInt()) * 100,
                ) // see this max value coming back here, we animate towards that value
                animation.duration = 1500 // in milliseconds
                animation.interpolator = DecelerateInterpolator()
                animation.start()

                //Вторая шкала "Температура по ощущениям"
                animation = ObjectAnimator.ofInt(
                    barFeelsLike,
                    "progress",
                    0,
                    abs(currentWeatherInfo.main.feels_like.toInt()) * 100
                )
                animation.duration = 1500 // in milliseconds
                animation.interpolator = DecelerateInterpolator()
                animation.start()

                mainActivity?.noNewRequest()
            } else {
                barMain.progress = abs(currentWeatherInfo.main.temp.toInt()) * 100
                barFeelsLike.progress = abs(currentWeatherInfo.main.feels_like.toInt()) * 100
            }
    }
}