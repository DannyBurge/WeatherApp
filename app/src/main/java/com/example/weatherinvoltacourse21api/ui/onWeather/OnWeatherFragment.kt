package com.example.weatherinvoltacourse21api.ui.onWeather

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.example.weatherinvoltacourse21api.MainActivity
import com.example.weatherinvoltacourse21api.R
import com.example.weatherinvoltacourse21api.databinding.FragmentWeatherBinding
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.roundToInt


class OnWeatherFragment : Fragment() {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val liveData: LiveData<String>? = mainActivity?.getCurrentInfo()
        liveData?.observe(viewLifecycleOwner, {
            binding.root.visibility = View.VISIBLE
            parseJsonSetText(it)
        })

        val isNewRequest: LiveData<Boolean>? = mainActivity?.isNewRequest()
        isNewRequest?.observe(viewLifecycleOwner, {
            doAnimation = it
        })
    }

    @SuppressLint("SetTextI18n")
    fun parseJsonSetText(result: String) {
        if (!result.contains("Unable", ignoreCase = true)) {
            val jsonResult = JSONObject(result)
            val cityName = jsonResult["name"]

            val jsonMain = jsonResult["main"].toString()
            val jsonSys = jsonResult["sys"].toString()
            val jsonWeather = (jsonResult["weather"] as JSONArray).getJSONObject(0)
            val sunrise = JSONObject(jsonSys)["sunrise"].toString().toFloat().toLong()
            val sunset = JSONObject(jsonSys)["sunset"].toString().toFloat().toLong()

            val temp = JSONObject(jsonMain)["temp"].toString().toFloat().roundToInt()
            val tempMin = JSONObject(jsonMain)["temp_min"].toString().toFloat().roundToInt()
            val tempMax = JSONObject(jsonMain)["temp_max"].toString().toFloat().roundToInt()
            val weatherInfo = jsonWeather["main"]

            val tempFeelsLike =
                JSONObject(jsonResult["main"].toString())["feels_like"].toString()
                    .toFloat().roundToInt()

            val windInfo = jsonResult["wind"].toString()
            val windSpeed = JSONObject(windInfo)["speed"]

            val visibility = (jsonResult["visibility"].toString().toFloat() / 1000).toInt()
            val humidity = JSONObject(jsonMain)["humidity"]
            val pressure =
                (JSONObject(jsonMain)["pressure"].toString().toFloat() * 0.75).toInt()

            val sdf = java.text.SimpleDateFormat("HH:mm")
            val sunriseTime = java.util.Date(sunrise * 1000)
            val sunsetTime = java.util.Date(sunset * 1000)

            binding.cityName.text = "$cityName"

            binding.tempMain.text = "${temp}°C"
            binding.tempMax.text = "Max temperature: ${tempMax}°C"
            binding.tempMin.text = "Min temperature: ${tempMin}°C"
            binding.tempFeelsLike.text = "Feels Like: ${tempFeelsLike}°C"

            binding.mainWeather.text = "${weatherInfo}"
            binding.sunRise.text = "Sunrise at " + sdf.format(sunriseTime)
            binding.sunSet.text = "Sunset at " + sdf.format(sunsetTime)
            binding.windInfo.text = "${windSpeed} m/s"
            binding.humidityInfo.text = "${humidity} %"
            binding.visibilityInfo.text = "${visibility} km"
            binding.pressureInfo.text = "${pressure} mm Hg"

            if (doAnimation) {
                val animation = ObjectAnimator.ofInt(
                    binding.tempHot,
                    "progress",
                    0,
                    abs(temp)*10
                ) // see this max value coming back here, we animate towards that value

                animation.duration = 1500 // in milliseconds

                animation.interpolator = DecelerateInterpolator()
                animation.start()

                mainActivity?.noNewRequest()
            } else binding.tempHot.progress = abs(temp)*10
        } else {
//            findViewById<LinearLayout>(R.id.internetProblemLayout).visibility = View.VISIBLE
        }

    }
}