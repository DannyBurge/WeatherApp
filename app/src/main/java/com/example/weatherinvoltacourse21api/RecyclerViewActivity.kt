package com.example.weatherinvoltacourse21api

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class RecyclerViewActivity : AppCompatActivity(), CellClickListener, SearchView.OnQueryTextListener {
    var citiesArray: MutableList<City> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        initList()
        initRecycler()
    }

    private fun initList() {
        val cities = resources.getStringArray(R.array.Cities)
        for (cityInfo in cities) {
            val name = cityInfo.split(",")[1]
            val country = cityInfo.split(",")[0]
            citiesArray.add(City(name, country))
        }
    }

    private fun initRecycler() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView,
        // попутно подавая в него обработчик событий клика
        recyclerView.adapter = CitiesRecyclerAdapter(this, citiesArray, this)
    }

    private fun updateCityView(filter: String) {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val filteredCities = citiesArray.filter { city ->
            city.city.startsWith(
                filter.toString(),
                ignoreCase = true
            )
        }
        recyclerView.adapter = CitiesRecyclerAdapter(this, filteredCities, this)
    }

    override fun onCellClickListener(data: City) {
        val intent = Intent()
        intent.putExtra("cityOrLocation", "q=${data.city}")
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

