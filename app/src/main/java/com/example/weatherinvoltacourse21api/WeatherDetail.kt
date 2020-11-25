package com.example.weatherinvoltacourse21api

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.round

class WeatherDetail : AppCompatActivity() {
    private var locationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)

        if (!isLocationPermissed()) {
            getLocalPermissions()
        } else getLastKnownLocation()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationRequest == null) {
                    return
                }

                val location = locationResult?.lastLocation
                if (locationResult != null) {
                    for (loc in locationResult.locations) {
                        requestWeather("lat=${location?.latitude}&lon=${location?.longitude}")
                    }
                }
            }
        }
        findViewById<TextView>(R.id.buttonLocationCity).setOnClickListener {
            getLastKnownLocation()
        }

        findViewById<Button>(R.id.tryAgainButton).setOnClickListener {
            if (!isLocationPermissed()) {
                getLocalPermissions()
            } else getLastKnownLocation()
        }

        findViewById<TextView>(R.id.buttonChangeCity).setOnClickListener {
            val intent = Intent(this, RecyclerViewActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private var onBuckPressed = 0
    override fun onBackPressed() {
        onBuckPressed += 1
        if (onBuckPressed > 1) {
            super.onBackPressed()
        } else Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        onBuckPressed = 0
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        locationProvider?.removeLocationUpdates(locationCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onBuckPressed = 0
        findViewById<LinearLayout>(R.id.layoutMain).visibility = View.GONE
        if (data == null) {
            getLastKnownLocation()
            return
        } else {
            val cityOrLocation = data.getStringExtra("cityOrLocation")
            if (cityOrLocation != null) {
                requestWeather("${cityOrLocation}")
            } else {
                if (resultCode == 0) {
                    val intent = Intent(this, RecyclerViewActivity::class.java)
                    startActivityForResult(intent, 1)
                }
            }
        }
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
        locationRequest?.interval = 10000
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

    fun getLocalPermissions() {
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

    private fun isLocationPermissed(): Boolean {
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

    private fun requestWeather(cityOrLocation: String) {
        val keyAPI = "3b5683c272c1dfb381272ff1d030cad3"
        GlobalScope.launch {
            val result =
                makeRequest(
                    "https://api.openweathermap.org/data/2.5/weather?"
                            + cityOrLocation +
                            "&units=metric&appid=${keyAPI}"
                )
            withContext(Dispatchers.Main) {
                parseJsonSetText(result)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun parseJsonSetText(result: String) {
        if (!result.contains("Unable", ignoreCase = true)) {
            val jsonResult = JSONObject(result)
            val cityName = jsonResult["name"]

            val jsonMain = jsonResult["main"].toString()
            val jsonSys = jsonResult["sys"].toString()
            val jsonWheather = (jsonResult["weather"] as JSONArray).getJSONObject(0)
            val sunrise = JSONObject(jsonSys)["sunrise"].toString().toFloat().toLong()
            val sunset = JSONObject(jsonSys)["sunset"].toString().toFloat().toLong()

            val temp = round(JSONObject(jsonMain)["temp"].toString().toFloat()).toInt()
            val temp_min = round(JSONObject(jsonMain)["temp_min"].toString().toFloat()).toInt()
            val temp_max = round(JSONObject(jsonMain)["temp_max"].toString().toFloat()).toInt()
            val wheatherInfo = jsonWheather["main"]

            val tempFeelsLike = round(
                JSONObject(jsonResult["main"].toString())["feels_like"].toString().toFloat()
            ).toInt()

            val windInfo = jsonResult["wind"].toString()
            val windSpeed = JSONObject(windInfo)["speed"]

            val visibility = (jsonResult["visibility"].toString().toFloat() / 1000).toInt()
            val humidity = JSONObject(jsonMain)["humidity"]
            val pressure = (JSONObject(jsonMain)["pressure"].toString().toFloat() * 0.75).toInt()

            val sdf = java.text.SimpleDateFormat("HH:mm")
            val sunriseTime = java.util.Date(sunrise * 1000)
            val sunsetTime = java.util.Date(sunset * 1000)

            findViewById<TextView>(R.id.cityName).text = "$cityName"
            findViewById<TextView>(R.id.tempMain).text = "${temp}°C"
            findViewById<TextView>(R.id.tempMax).text = "Max temperature: ${temp_max}°C"
            findViewById<TextView>(R.id.tempMin).text = "Min temperature: ${temp_min}°C"
            findViewById<TextView>(R.id.tempFeelsLike).text = "Feels Like: ${tempFeelsLike}°C"

            findViewById<TextView>(R.id.mainWeather).text = "${wheatherInfo}"
            findViewById<TextView>(R.id.sunRise).text = "Sunrise at " + sdf.format(sunriseTime)
            findViewById<TextView>(R.id.sunSet).text = "Sunset at " + sdf.format(sunsetTime)


            findViewById<TextView>(R.id.windInfo).text = "${windSpeed} m/s"
            findViewById<TextView>(R.id.humidityInfo).text = "${humidity} %"
            findViewById<TextView>(R.id.visibilityInfo).text = "${visibility} km"
            findViewById<TextView>(R.id.pressureInfo).text = "${pressure} mm Hg"

            findViewById<LinearLayout>(R.id.layoutMain).visibility = View.VISIBLE
        } else {
            findViewById<LinearLayout>(R.id.internetProblemLayout).visibility = View.VISIBLE
        }
    }
}