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


class RecyclerViewActivity : AppCompatActivity(), CellClickListener {
    private lateinit var binding: ActivityCitychangeBinding
    private lateinit var adapterFav: FavouriteCitiesRecyclerAdapter
    private lateinit var adapterCit: CitiesRecyclerAdapter

    // Списки городов
    private var citiesArray: MutableList<City> = ArrayList()
    private val favouriteCitiesArray = MutableLiveData<MutableList<City>>()
    private var filteredCities: MutableList<City>? = null
    fun getFavouriteList(): LiveData<MutableList<City>> {
        return favouriteCitiesArray
    }

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_citychange)

        // На набор текста вешаем обновление списка городов по фильтру
        binding.cityFilter.addTextChangedListener {
            updateCityView(binding.cityFilter.text.toString())
        }

        initList()
        initRecycler()
    }

    // Начальные списки городов
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

    // Устанавливаем наши адаптеры в качестве адаптеров для двух РесайклВью,
    // попутно подавая в них обработчик событий клика
    private fun initRecycler() {
        adapterFav = FavouriteCitiesRecyclerAdapter(this, favouriteCitiesArray, this)
        adapterCit = CitiesRecyclerAdapter(this, filteredCities, adapterFav, this)
        binding.listFavouriteCitiesRecView.adapter = adapterFav
        binding.listCitiesRecView.adapter = adapterCit
    }

    // Сохраняем список предпочитаемых городов, с одним городом по умолчанию
    override fun onDestroy() {
        var stringsForSaving = ""
        for (favCity in favouriteCitiesArray.value!!) {
            stringsForSaving += "${favCity.city},${favCity.country},${favCity.longitude},${favCity.latitude},${favCity.isFavourite};"
        }
        prefs.edit().putString("savedCities", stringsForSaving.dropLast(1)).apply()
        super.onDestroy()
    }

    // Фильтруем список городов при вводе буковок в поле ЕдитТекст
    private fun updateCityView(filter: String) {
        filteredCities = if (filter.isNotEmpty()) {
            citiesArray.filter { city ->
                city.city.startsWith(
                    filter,
                    ignoreCase = true
                )
            } as MutableList<City>
        } else null
        binding.listCitiesRecView.adapter =
            filteredCities?.let { CitiesRecyclerAdapter(this, it, adapterFav, this) }
    }

    // При нажатии на город, возвращаем его в главное активити
    override fun onCellClickListener(data: City) {
        val intent = Intent()
        intent.putExtra("Location", floatArrayOf(data.latitude, data.longitude))
        setResult(RESULT_OK, intent)
        finish()
    }
}

