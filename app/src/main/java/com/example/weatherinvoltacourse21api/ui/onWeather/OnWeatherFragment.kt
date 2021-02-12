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


class OnWeatherFragment : Fragment() {

    private var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentWeatherBinding
    private var durationFirst: Long = 0
    private var durationSecond: Long = 0

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
        // Берем данные из полученного запроса и вешаем слушатель на их изменение
        liveData?.observe(viewLifecycleOwner) {
            binding.root.visibility = View.VISIBLE
            // Отображаем анимацию, если в момент обновления мы находимся на текущей вкладке
            if (mainActivity?.binding?.viewPager?.currentItem == 1) {
                if (mainActivity?.isNewRequest == true) {
                    durationFirst = 1500
                    durationSecond = 2000
                } else {
                    durationFirst = 0
                    durationSecond = 0
                }
            }
            setText(it)
        }
    }

    override fun onPause() {
        durationFirst = 0
        durationSecond = 0
        super.onPause()
    }

    @SuppressLint("SetTextI18n")
    fun setText(currentWeatherInfo: CurrentWeatherData) {
        // В зависимости от температуры используем нужные бары, а остальные прячем
        val barMain: ProgressBar
        val barFeelsLike: ProgressBar

        if (currentWeatherInfo.main.temp < 0) {
            barMain = binding.barOutBlue
            binding.barOutRed.visibility = View.INVISIBLE

        } else {
            barMain = binding.barOutRed
            binding.barOutBlue.visibility = View.INVISIBLE
        }

        if (currentWeatherInfo.main.feels_like < 0) {
            barFeelsLike = binding.barInBlue
            binding.barInRed.visibility = View.INVISIBLE
        } else {
            barFeelsLike = binding.barInRed
            binding.barInBlue.visibility = View.INVISIBLE
        }

        barMain.visibility = View.VISIBLE
        barFeelsLike.visibility = View.VISIBLE

        //Первая шкала "Температура"
        var animation = ObjectAnimator.ofInt(
            barMain,
            "progress",
            0,
            abs(currentWeatherInfo.main.temp.toInt()) * 100,
        )
        animation.duration = durationFirst //1500 или 0
        animation.interpolator = DecelerateInterpolator()
        animation.start()

        //Вторая шкала "Температура по ощущениям"
        animation = ObjectAnimator.ofInt(
            barFeelsLike,
            "progress",
            0,
            abs(currentWeatherInfo.main.feels_like.toInt()) * 100
        )
        animation.duration = durationSecond //2000 или 0
        animation.interpolator = DecelerateInterpolator()
        animation.start()

        val sdf = java.text.SimpleDateFormat("HH:mm")

        binding.cityName.text = currentWeatherInfo.name

        binding.tempMain.text = "${currentWeatherInfo.main.temp.toInt()}°C"
        binding.tempMax.text = "${currentWeatherInfo.main.temp_max.toInt()}°C"
        binding.tempMin.text = "${currentWeatherInfo.main.temp_min.toInt()}°C"
        binding.tempFeelsLike.text =
            "Feels Like: ${currentWeatherInfo.main.feels_like.toInt()}°C"

        binding.mainWeather.text = currentWeatherInfo.weather[0].description
        binding.sunRise.text = sdf.format(java.util.Date(currentWeatherInfo.sys.sunrise * 1000))
        binding.sunSet.text = sdf.format(java.util.Date(currentWeatherInfo.sys.sunset * 1000))
        binding.windInfo.text = "${currentWeatherInfo.wind.speed} m/s"
        binding.humidityInfo.text = "${currentWeatherInfo.main.humidity} %"
        binding.visibilityInfo.text = "${(currentWeatherInfo.visibility / 1000).toInt()} km"
        binding.pressureInfo.text = "${currentWeatherInfo.main.pressure} mmHg"
    }
}