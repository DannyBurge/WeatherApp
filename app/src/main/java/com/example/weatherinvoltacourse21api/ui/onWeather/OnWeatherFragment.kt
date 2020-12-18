package com.example.weatherinvoltacourse21api.ui.onWeather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherinvoltacourse21api.R
import timber.log.Timber

class OnWeatherFragment : Fragment() {

    private lateinit var onWeatherViewModel: OnWeatherViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Timber.i("Fragment onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onWeatherViewModel = ViewModelProvider(this).get(OnWeatherViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_weather, container, false)

        Timber.i("OnWeather Fragment onCreateView")
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("OnWeather Fragment onSaveInstanceState")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Timber.i("OnWeather Fragment onViewStateRestored")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("OnWeather Fragment onPause")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("OnWeather Fragment onResume")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("OnWeather Fragment onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("OnWeather Fragment onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("OnWeather Fragment onDestroyView")
    }
}