package com.example.weatherinvoltacourse21api

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class RecyclerViewActivity : AppCompatActivity(), CellClickListener, SearchView.OnQueryTextListener {
    private var citiesArray: MutableList<City> = ArrayList()
    private var favouriteCitiesArray: MutableList<City> = ArrayList()
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

        prefs =
            getSharedPreferences("savedCities", Context.MODE_PRIVATE)

        initList()
        initRecycler()
    }

    private fun initList() {
            citiesArray.add(City("Moscow", "Russia",37.62f,55.75f))
            citiesArray.add(City("Perm", "Russia",56.29f,58.02f))
            citiesArray.add(City("Vorkuta", "Russia",64.06f,67.50f))
            citiesArray.add(City("Sacramento", "USA",-121.48f,38.58f))
            citiesArray.add(City("Abu dhabi", "UAE",54.37f,24.47f))
            citiesArray.add(City("Perth", "Australia",115.86f,-31.95f))

        favouriteCitiesArray.add(City("Auto", "locate", 0f,0f))
    }

    private fun initRecycler() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val recyclerFavsView: RecyclerView = findViewById(R.id.recycler_favourite_view)
        //устанавливаем наш адаптер в качестве адаптера для нашего RecyclerView,
        // попутно подавая в него обработчик событий клика
        recyclerView.adapter = CitiesRecyclerAdapter(this, citiesArray, this)
        recyclerFavsView.adapter = CitiesRecyclerAdapter(this, favouriteCitiesArray, this)
    }

    override fun onResume() {
        super.onResume()
        if(prefs.contains("favouriteCities")){
            // Получаем число из настроек
//            favouriteCitiesArray.addAll(prefs.getStringSet("favouriteCities", favouriteCitiesArray))
        }
    }

    private fun updateCityView(filter: String) {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val filteredCities = citiesArray.filter { city ->
            city.city.startsWith(
                filter.toString(),
                ignoreCase = true
            )
        } as MutableList<City>
        recyclerView.adapter = CitiesRecyclerAdapter(this, filteredCities, this)
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

