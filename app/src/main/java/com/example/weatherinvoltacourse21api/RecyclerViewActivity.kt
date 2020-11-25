package com.example.weatherinvoltacourse21api

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class RecyclerViewActivity : AppCompatActivity(), CellClickListener {
    var citiesArray: MutableList<City> = ArrayList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_recycler)

        findViewById<EditText>(R.id.cityFilter).addTextChangedListener {
            updateCityView()
        }

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

    private fun updateCityView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val filter = findViewById<EditText>(R.id.cityFilter).text
        if (filter != null) {
            val fileredCities = citiesArray.filter { city ->
                city.city.startsWith(
                    filter.toString(),
                    ignoreCase = true
                )
            }
            (recyclerView.adapter as CitiesRecyclerAdapter).setItems(fileredCities)
            recyclerView.adapter = CitiesRecyclerAdapter(this, fileredCities, this)
        }
    }

    override fun onCellClickListener(data: City) {
        val intent = Intent()
        intent.putExtra("cityOrLocation", "q=${data.city}")
        setResult(RESULT_OK, intent)
        finish()
    }
}

