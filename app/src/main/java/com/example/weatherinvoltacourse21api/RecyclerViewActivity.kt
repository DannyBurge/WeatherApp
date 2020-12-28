package com.example.weatherinvoltacourse21api

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class RecyclerViewActivity : AppCompatActivity(), CellClickListener, SearchView.OnQueryTextListener {

    private lateinit var recyclerFavsView: RecyclerView
    private lateinit var adapter: FavouriteCitiesRecyclerAdapter

    private var citiesArray: MutableList<City> = ArrayList()
    private val favouriteCitiesArray = MutableLiveData<MutableList<City>>()
    fun getFavouriteList(): LiveData<MutableList<City>> {
        return favouriteCitiesArray
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var prefs: SharedPreferences


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cityselection_searchbar, menu)
        val searchView: SearchView = (menu?.findItem(R.id.app_bar_search)?.actionView as SearchView)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        searchView.isIconifiedByDefault = true
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citychange)
        setSupportActionBar(findViewById(R.id.listToolbar))
        recyclerFavsView = findViewById(R.id.listFavouriteCitiesRecView)

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
        val recyclerView: RecyclerView = findViewById(R.id.listCitiesRecView)
        //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView,
        // попутно подавая в него обработчик событий клика
        adapter = FavouriteCitiesRecyclerAdapter(this, favouriteCitiesArray, this)
        recyclerFavsView.adapter = adapter
        recyclerView.adapter = CitiesRecyclerAdapter(this, citiesArray, adapter, this)
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
        val recyclerView: RecyclerView = findViewById(R.id.listCitiesRecView)
        val filteredCities = citiesArray.filter { city ->
            city.city.startsWith(
                filter,
                ignoreCase = true
            )
        } as MutableList<City>
        recyclerView.adapter =
            CitiesRecyclerAdapter(this, filteredCities, adapter, this)
    }

    override fun onCellClickListener(data: City) {
        val intent = Intent()
        intent.putExtra("Location", "lat=${data.latitude}&lon=${data.longitude}")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        updateCityView(newText)
        return false
    }
}

