package com.example.weatherinvoltacourse21api

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
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
import java.io.Serializable


class MainActivity : AppCompatActivity() {
    // Переменки для геолокации
    private var locationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private lateinit var currentLocation: FloatArray

    lateinit var binding: ActivityMainBinding

    // Перменки для запросов
    lateinit var retrofit: Retrofit
    lateinit var weatherInfoAPI: WeatherInfoAPI

    private val newRequest = MutableLiveData<Boolean>()
    fun isNewRequest(): LiveData<Boolean> {
        return newRequest
    }

    fun noNewRequest() {
        newRequest.value = false
    }

    // Диалог если интернет бо-бо
    lateinit var dialog: AlertDialog.Builder
    var isDialogShowing = false

    // Данные о текущей погоде
    private var weatherCurrentInfo = MutableLiveData<CurrentWeatherData>()
    fun getCurrentInfo(): LiveData<CurrentWeatherData> {
        return weatherCurrentInfo
    }

    // Данные о почасовой погоде
    private var weatherHourlyInfo = MutableLiveData<List<HourWeatherData>>()
    fun getHourlyInfo(): LiveData<List<HourWeatherData>> {
        return weatherHourlyInfo
    }

    // Данные о ежедневной погоде
    private var weatherWeeklyInfo = MutableLiveData<List<DayWeatherData>>()
    fun getWeeklyInfo(): LiveData<List<DayWeatherData>> {
        return weatherWeeklyInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)

        // Создаем диалог про плохой интернет, на кнопку вешаем повтор запроса
        dialog = AlertDialog.Builder(this)
        dialog.setMessage(resources.getString(R.string.InternetIssue))
        dialog.setPositiveButton("Retry") { _, _ ->
            isDialogShowing = false
            if (!isLocationGranted()) {
                getLocalPermissions()
            } else checkIfThereIsFavouriteCity()
        }
        dialog.create()

        // Формируем запрос и прикручиваем API
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherInfoAPI = retrofit.create(WeatherInfoAPI::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Проверяем есть ли в сейве данные (изменилась ли конфигурация)
        if (savedInstanceState != null) {
            weatherCurrentInfo.value = savedInstanceState.getParcelable("CurrentWeatherData")
            weatherHourlyInfo.value =
                savedInstanceState.getSerializable("HourWeatherData") as List<HourWeatherData>
            weatherWeeklyInfo.value =
                savedInstanceState.getSerializable("DayWeatherData") as List<DayWeatherData>
            currentLocation = savedInstanceState.getFloatArray("currentLocation")!!

            savedInstanceState.clear()

            // Взяли из сейва и отображаем
            binding.viewPager.visibility = View.VISIBLE
        } else {
            binding.viewPager.visibility = View.INVISIBLE
            // В сейве пусто, чекаем список городов
            checkIfThereIsFavouriteCity()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {}
            }
        }

        // Свайп вниз для обновления
        binding.swipeRefresh.setOnRefreshListener {
            requestWeather(currentLocation)
        }
        // Цвета для виджета обновления
        binding.swipeRefresh.setColorSchemeResources(
            R.color.material_red_400,
            R.color.material_blue_400,
        )

        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.refreshBackground))


        // Привязываем отображение фрагментов к кнопулькам внизу
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
                    true
                }
                else -> false
            }
        }

        // Добавляем листалку свайпами
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            // Говорим подсвечивать нужную кнопку, если свайпом была изменена вкладка
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

        // Добавляем наши фрагменты в адаптер
        val adapter = FragmentsPagerAdapter(supportFragmentManager)
        adapter.addFragment(OnHourlyFragment())
        adapter.addFragment(OnWeatherFragment())
        adapter.addFragment(OnWeeklyFragment())
        binding.viewPager.adapter = adapter

        // Дефолтная вкладка - центральная
        binding.viewPager.currentItem = 1
        binding.bottomNavigation.menu.findItem(R.id.navigation_onWeatherFragment).isChecked = true
    }

    // Вернулся результат с выбора города
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onBuckPressed = 0
        if (data == null) {
            return
        } else {
            currentLocation = data.getFloatArrayExtra("Location")!!
            requestWeather(currentLocation)
        }
    }

    // Двойное нажатие на кнопку назад - выход из приложения
    private var onBuckPressed = 0
    override fun onBackPressed() {
        onBuckPressed += 1
        if (onBuckPressed > 1) {
            super.onBackPressed()
        } else Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show()
    }

    // Не смотрим геопозицию, если приложение свернуто или выключено
    override fun onPause() {
        super.onPause()
        locationProvider?.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        onBuckPressed = 0
        super.onResume()
    }

    // Получаем геопозицию из гугл сервиса
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        locationProvider?.requestLocationUpdates(locationRequest, locationCallback, null)
        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        // При успехе запрашиваем погоду
        locationProvider?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation =
                    floatArrayOf(location.latitude.toFloat(), location.longitude.toFloat())
                requestWeather(currentLocation)
            }
        }

        locationRequest = LocationRequest()
        locationRequest?.interval = 60000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // Просим юзера включить геопозицию, если она выключена
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

    // Запрос юзера на получение разрешения на доступ к геолокации
    private fun getLocalPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            ), 2
        )
    }

    // Реакция на получение или запрет на доступ к геолокации
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Можно
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                    getLastKnownLocation()
                } else {
                    // Нельзя (давай спросим город)
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RecyclerViewActivity::class.java)
                    startActivityForResult(intent, 1)
                }
            }
            else -> {
            }
        }
    }

    // Проверка даны ли уже разрешения на доступ к геолокации
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

    // Проверяем, какой город отмечен, как город по умолчанию и проверяем погоду для него
    private fun checkIfThereIsFavouriteCity() {
        val prefs = getSharedPreferences("savedCities", Context.MODE_PRIVATE)
        val savedCities =
            prefs.getString("savedCities", "Auto,locate,${0f},${0f},true")
        if (savedCities != null) {
            // Так как он такой только один, то идем до первого попавшегося
            for (savedCity in savedCities.split(";")) {
                val cityInfo = savedCity.split(",")
                if (cityInfo[4].toBoolean()) {
                    if ("lat=${cityInfo[3]}&lon=${cityInfo[2]}" == "lat=0.0&lon=0.0") {
                        if (!isLocationGranted()) getLocalPermissions() else getLastKnownLocation()
                    } else {
                        currentLocation = floatArrayOf(cityInfo[3].toFloat(), cityInfo[2].toFloat())
                        requestWeather(currentLocation)
                    }
                    break
                }
            }
        }
    }

    // Запрос погоды
    private fun requestWeather(location: FloatArray) {
        // Если координаты нулевые, то значит, что нужно запросить геолокацию а потом вернуться обратно
        if ((currentLocation[0] == 0f) and (currentLocation[1] == 0f)) {
            if (!isLocationGranted()) getLocalPermissions() else getLastKnownLocation()
        }

        val isLoading = arrayOf(true, true)
        binding.swipeRefresh.isRefreshing = true
//        binding.loadingProgressBar.show()

        newRequest.postValue(true)

        // Асинхронный запрос текущей погоды
        weatherInfoAPI.getCurrentWeatherInfo(location[0], location[1])
            ?.enqueue(object : Callback<CurrentWeatherData?> {
                // Неуспешный запрос, показываем диалог
                override fun onFailure(call: Call<CurrentWeatherData?>, t: Throwable) {
                    if (!isDialogShowing) dialog.show()
//                    binding.loadingProgressBar.hide()
                }

                // Успешный запрос
                override fun onResponse(
                    call: Call<CurrentWeatherData?>,
                    response: Response<CurrentWeatherData?>
                ) {
                    if (response.code() == 200) {
                        weatherCurrentInfo.postValue(response.body()!!)
                        isLoading[0] = false
                    } else dialog.show()
                }
            })

        // Асинхронный запрос ежедневной и почасовой погоды
        weatherInfoAPI.getHourlyDailyWeatherInfo(location[0], location[1])
            ?.enqueue(object : Callback<OneCallWeatherData?> {
                // Неуспешный запрос, показываем диалог
                override fun onFailure(call: Call<OneCallWeatherData?>, t: Throwable) {
                    if (!isDialogShowing) dialog.show()
//                    binding.loadingProgressBar.hide()
                }

                // Успешный запрос
                override fun onResponse(
                    call: Call<OneCallWeatherData?>,
                    response: Response<OneCallWeatherData?>
                ) {
                    if (response.code() == 200) {
                        weatherHourlyInfo.postValue(response.body()!!.hourly)
                        weatherWeeklyInfo.postValue(response.body()!!.daily)
                        isLoading[1] = false
                    } else dialog.show()
                }
            })

        // Жди, когда запросы завершатся и выключай прогресбар, показывай фрагменты
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                while (true) {
                    if (!isLoading[0] and !isLoading[1]) {
//                        binding.loadingProgressBar.hide()
                        binding.swipeRefresh.isRefreshing = false
                        binding.viewPager.visibility = View.VISIBLE
                        break
                    }
                    delay(300)
                }
            }
        }
    }

    override fun onCreateDialog(id: Int): Dialog {
        isDialogShowing = true
        return super.onCreateDialog(id)
    }

    // Сохраняем данные о погоде для смены конфигурации
    override fun onSaveInstanceState(outState: Bundle) {
        if (weatherCurrentInfo.value != null) {
            outState.putParcelable("CurrentWeatherData", weatherCurrentInfo.value as Parcelable)
            outState.putSerializable("HourWeatherData", weatherHourlyInfo.value as Serializable)
            outState.putSerializable("DayWeatherData", weatherWeeklyInfo.value as Serializable)
            outState.putFloatArray("currentLocation", currentLocation)
        }
        Timber.i("onSaveInstanceState Called")
        super.onSaveInstanceState(outState)
    }


}