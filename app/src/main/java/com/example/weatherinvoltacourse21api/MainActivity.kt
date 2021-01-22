package com.example.weatherinvoltacourse21api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.test_stuff.FragmentsPagerAdapter
import com.example.weatherinvoltacourse21api.databinding.ActivityMainBinding
import com.example.weatherinvoltacourse21api.ui.onHourly.OnHourlyFragment
import com.example.weatherinvoltacourse21api.ui.onWeather.OnWeatherFragment
import com.example.weatherinvoltacourse21api.ui.onWeekly.OnWeeklyFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private var cityName: String? = null

    private var locationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    lateinit var binding: ActivityMainBinding

    lateinit var retrofit: Retrofit
    lateinit var weatherInfoAPI: WeatherInfoAPI

    private val newRequest = MutableLiveData<Boolean>()
    fun isNewRequest(): LiveData<Boolean> {
        return newRequest
    }

    fun noNewRequest() {
        newRequest.value = false
    }

    private var weatherCurrentInfo = MutableLiveData<CurrentWeatherData>()
    fun getCurrentInfo(): LiveData<CurrentWeatherData> {
        return weatherCurrentInfo
    }

    private var weatherHourlyInfo = MutableLiveData<List<HourWeatherData>>()
    fun getHourlyInfo(): LiveData<List<HourWeatherData>> {
        return weatherHourlyInfo
    }

    private var weatherWeeklyInfo = MutableLiveData<List<DayWeatherData>>()
    fun getWeeklyInfo(): LiveData<List<DayWeatherData>> {
        return weatherWeeklyInfo
    }

    private fun checkIfThereIsFavouriteCity() {
        val prefs = getSharedPreferences("savedCities", Context.MODE_PRIVATE)
        val savedCities =
            prefs.getString("savedCities", "Auto,locate,${0f},${0f},true")
        if (savedCities != null) {
            for (savedCity in savedCities.split(";")) {
                val cityInfo = savedCity.split(",")
                if (cityInfo[4].toBoolean()) {
                    if ("lat=${cityInfo[3]}&lon=${cityInfo[2]}" == "lat=0.0&lon=0.0") {
                        getLastKnownLocation()
                    } else requestWeather(floatArrayOf(cityInfo[2].toFloat(),cityInfo[3].toFloat()))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherInfoAPI = retrofit.create(WeatherInfoAPI::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.containerSecond.visibility = View.INVISIBLE
        binding.viewPager.visibility = View.INVISIBLE

        if (!isLocationGranted()) {
            getLocalPermissions()
        } else checkIfThereIsFavouriteCity()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationRequest == null) {
                    return
                }
            }
        }

        binding.tryAgainButton.setOnClickListener {
            if (!isLocationGranted()) {
                getLocalPermissions()
            } else checkIfThereIsFavouriteCity()
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_onHourlyFragment -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.navigation_onWeatherFragment -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.navigation_onWeeklyFragment -> {
                    binding.viewPager.currentItem = 2
//                    parse2ndJsonSetText(weatherOtherInfoJSON!!)
                    true
                }
                else -> false
            }
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding.bottomNavigation.menu.findItem(R.id.navigation_onHourlyFragment).isChecked =
                        true
                    1 -> {
                        binding.bottomNavigation.menu.findItem(R.id.navigation_onWeatherFragment).isChecked =
                            true
                    }
                    2 -> binding.bottomNavigation.menu.findItem(R.id.navigation_onWeeklyFragment).isChecked =
                        true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        val adapter = FragmentsPagerAdapter(supportFragmentManager)
        adapter.addFragment(OnHourlyFragment())
        adapter.addFragment(OnWeatherFragment())
        adapter.addFragment(OnWeeklyFragment())
        binding.viewPager.adapter = adapter

        binding.viewPager.currentItem = 1
        binding.bottomNavigation.menu.findItem(R.id.navigation_onWeatherFragment).isChecked = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onBuckPressed = 0
        if (data == null) {
            return
        } else {
            val location = data.getFloatArrayExtra("Location")
            cityName = data.getStringExtra("CityName")
            if (location != null) {
                if ((location[0] == 0f) and (location[1] == 0f)) {
                    getLastKnownLocation()
                } else {
                    requestWeather(location)
                }
            } else {
                if (resultCode == 0) {
                    val intent = Intent(this, RecyclerViewActivity::class.java)
                    startActivityForResult(intent, 1)
                }
            }
        }
    }

    private var onBuckPressed = 0
    override fun onBackPressed() {
        onBuckPressed += 1
        if (onBuckPressed > 1) {
            super.onBackPressed()
        } else Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        locationProvider?.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        onBuckPressed = 0
        super.onResume()
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        locationProvider?.requestLocationUpdates(locationRequest, locationCallback, null)
        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        locationProvider?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                requestWeather(floatArrayOf(location.latitude.toFloat(),location.longitude.toFloat()))
            }
        }

        locationRequest = LocationRequest()
        locationRequest?.interval = 60000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, 500)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    private fun getLocalPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            ), 2
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                    getLastKnownLocation()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RecyclerViewActivity::class.java)
                    startActivityForResult(intent, 1)
                }
            }
            else -> {
            }
        }
    }

    private fun isLocationGranted(): Boolean {
        return ((ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
                &&
                (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED))
    }

    private fun requestWeather(location: FloatArray) {
        val isLoading = arrayOf(true, true)
        binding.loadingProgressBar.show()
        binding.containerSecond.visibility = View.GONE

        newRequest.postValue(true)

        weatherInfoAPI.getCurrentWeatherInfo(location[0], location[1])?.enqueue(object : Callback<CurrentWeatherData?> {
            override fun onFailure(call: Call<CurrentWeatherData?>, t: Throwable) {
                binding.containerSecond.visibility = View.VISIBLE
                binding.loadingProgressBar.hide()
            }

            override fun onResponse(
                call: Call<CurrentWeatherData?>,
                response: Response<CurrentWeatherData?>
            ) {
                weatherCurrentInfo.postValue(response.body()!!)
                isLoading[0] = false
            }
        })

        weatherInfoAPI.getHourlyDailyWeatherInfo(location[0], location[1])?.enqueue(object : Callback<OneCallWeatherData?> {
            override fun onFailure(call: Call<OneCallWeatherData?>, t: Throwable) {
                binding.containerSecond.visibility = View.VISIBLE
                binding.loadingProgressBar.hide()
            }

            override fun onResponse(
                call: Call<OneCallWeatherData?>,
                response: Response<OneCallWeatherData?>
            ) {
                weatherHourlyInfo.postValue(response.body()!!.hourly)
                weatherWeeklyInfo.postValue(response.body()!!.daily)
                isLoading[1] = false
            }
        })

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                while (true) {
                    if (!isLoading[0] and !isLoading[1]) {
                        binding.loadingProgressBar.hide()
                        binding.viewPager.visibility = View.VISIBLE
                        binding.containerSecond.visibility = View.GONE
                        break
                    }
                    delay(300)
                }
            }
        }
    }
}