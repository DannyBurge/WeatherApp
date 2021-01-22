package com.example.weatherinvoltacourse21api

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherinvoltacourse21api.databinding.ActivityCitychangeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class RecyclerViewActivity : AppCompatActivity(), CellClickListener {
    private lateinit var binding: ActivityCitychangeBinding
    private lateinit var adapter: FavouriteCitiesRecyclerAdapter

    private var citiesArray: MutableList<City> = ArrayList()
    private val favouriteCitiesArray = MutableLiveData<MutableList<City>>()
    fun getFavouriteList(): LiveData<MutableList<City>> {
        return favouriteCitiesArray
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_citychange)

        binding.cityFilter.addTextChangedListener {
            updateCityView(binding.cityFilter.text.toString())
        }

        initList()
        initRecycler()
    }

    private fun initList() {
        citiesArray.add(City("Moscow", "Russia", 37.62f, 55.75f, false))
        citiesArray.add(City("Perm", "Russia", 56.29f, 58.02f, false))
        citiesArray.add(City("Vorkuta", "Russia", 64.06f, 67.50f, false))
        citiesArray.add(City("Sacramento", "USA", -121.48f, 38.58f, false))
        citiesArray.add(City("Abu dhabi", "UAE", 54.37f, 24.47f, false))
        citiesArray.add(City("Perth", "Australia", 115.86f, -31.95f, false))

        prefs = getSharedPreferences("savedCities", Context.MODE_PRIVATE)
        val savedCities =
            prefs.getString("savedCities", "Auto,locate,${0f},${0f},true")
        favouriteCitiesArray.value = mutableListOf()
        if (savedCities != null) {
            for (savedCity in savedCities.split(";")) {
                val cityInfo = savedCity.split(",")
                favouriteCitiesArray.value?.add(
                    City(
                        cityInfo[0],
                        cityInfo[1],
                        cityInfo[2].toFloat(),
                        cityInfo[3].toFloat(),
                        cityInfo[4].toBoolean()
                    )
                )
            }
        }
    }

    private fun initRecycler() {
        //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView,
        // попутно подавая в него обработчик событий клика
        adapter = FavouriteCitiesRecyclerAdapter(this, favouriteCitiesArray, this)
        binding.listFavouriteCitiesRecView.adapter = adapter
    }


    override fun onDestroy() {

        var stringsForSaving = ""
        for (favCity in favouriteCitiesArray.value!!) {
            stringsForSaving += "${favCity.city},${favCity.country},${favCity.longitude},${favCity.latitude},${favCity.isFavourite};"
        }
        prefs.edit().putString("savedCities", stringsForSaving.dropLast(1)).apply()
        super.onDestroy()
    }

    private fun updateCityView(filter: String) {
        var filteredCities: MutableList<City>? = null
        if (filter.isNotEmpty()) {
            filteredCities = citiesArray.filter { city ->
                city.city.startsWith(
                    filter,
                    ignoreCase = true
                )
            } as MutableList<City>
        }
        binding.listCitiesRecView.adapter =
            filteredCities?.let { CitiesRecyclerAdapter(this, it, adapter, this) }
    }

    override fun onCellClickListener(data: City) {
        val intent = Intent()
        intent.putExtra("Location", floatArrayOf(data.latitude,data.longitude))
        setResult(RESULT_OK, intent)
        finish()
    }
}

