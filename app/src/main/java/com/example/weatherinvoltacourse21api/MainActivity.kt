package com.example.weatherinvoltacourse21api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.test_stuff.FragmentsPagerAdapter
import com.example.weatherinvoltacourse21api.ui.onHourly.OnHourlyFragment
import com.example.weatherinvoltacourse21api.ui.onWeather.OnWeatherFragment
import com.example.weatherinvoltacourse21api.ui.onWeekly.OnWeeklyFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var locationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null


    private val newRequest = MutableLiveData<Boolean>()
    fun isNewRequest(): LiveData<Boolean> {
        return newRequest
    }
    fun noNewRequest() {
        newRequest.value = false
    }

    private val weatherCurrentInfoJSON = MutableLiveData<String>()
    fun getCurrentInfo(): LiveData<String> {
        return weatherCurrentInfoJSON
    }

    private val weatherHourlyInfoJSON = MutableLiveData<String>()
    fun getHourlyInfo(): LiveData<String> {
        return weatherHourlyInfoJSON
    }

    private val weatherWeeklyInfoJSON = MutableLiveData<String>()
    fun getWeeklyInfo(): LiveData<String> {
        return weatherWeeklyInfoJSON
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        if (!isLocationGranted()) {
            getLocalPermissions()
        } else getLastKnownLocation()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationRequest == null) {
                    return
                }
            }
        }

        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.buttonLocationCity)

        fab.setOnClickListener {
            val intent = Intent(this, RecyclerViewActivity::class.java)
            startActivityForResult(intent, 1)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_onHourlyFragment -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.navigation_onWeatherFragment -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.navigation_onWeeklyFragment -> {
                    viewPager.currentItem = 2
//                    parse2ndJsonSetText(weatherOtherInfoJSON!!)
                    true
                }
                else -> false
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigation.menu.findItem(R.id.navigation_onHourlyFragment).isChecked =
                        true
                    1 -> {
                        bottomNavigation.menu.findItem(R.id.navigation_onWeatherFragment).isChecked =
                            true
                    }
                    2 -> bottomNavigation.menu.findItem(R.id.navigation_onWeeklyFragment).isChecked =
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
        viewPager.adapter = adapter

        viewPager.currentItem = 1
        bottomNavigation.menu.findItem(R.id.navigation_onWeatherFragment).isChecked = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onBuckPressed = 0
        if (data == null) {
            return
        } else {
            val location = data.getStringExtra("Location")
            if (location != null) {
                if (location == "lat=0.0&lon=0.0") {
                    getLastKnownLocation()
                } else {
                    requestWeather("$location")
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
                requestWeather("lat=${location.latitude}&lon=${location.longitude}")
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

    private fun requestWeather(location: String) {
        Timber.i("requestWeather")
        newRequest.postValue(true)
        val keyAPI = "3b5683c272c1dfb381272ff1d030cad3"
        GlobalScope.launch {
            weatherCurrentInfoJSON.postValue(
//            weatherCurrentInfoJSON =
                makeRequest(
                    ("https://api.openweathermap.org/data/2.5/weather?"
                            + location) +
                            "&units=metric&appid=${keyAPI}"
                )
            )
        }
        GlobalScope.launch {
            weatherHourlyInfoJSON.postValue(
                makeRequest(
                    "https://api.openweathermap.org/data/2.5/onecall?"
                            + location +
                            "&exclude=current,minutely,daily,alerts&units=metric&appid=${keyAPI}"
                )
            )
        }
        GlobalScope.launch {
            weatherWeeklyInfoJSON.postValue(
                makeRequest(
                    "https://api.openweathermap.org/data/2.5/onecall?"
                            + location +
                            "&exclude=current,minutely,hourly,alerts&units=metric&appid=${keyAPI}"
                )
            )
        }
    }
}