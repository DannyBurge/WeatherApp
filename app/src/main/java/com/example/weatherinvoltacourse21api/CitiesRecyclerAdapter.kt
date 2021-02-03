package com.example.weatherinvoltacourse21api

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView

//Создаем класс CitiesRecyclerAdapter и наследуем его от класса RecyclerView.Adapter,
//добавляем на вход обработчик событий
class CitiesRecyclerAdapter(
    context: Context,
    private val cities: MutableList<City>?,
    favouriteCitiesAdapter: FavouriteCitiesRecyclerAdapter,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<CitiesRecyclerAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    val localContext = context
    val adapter = favouriteCitiesAdapter

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.city_list_item, parent, false)
        return ViewHolder(view)
    }

    //Задаем значения для элемента списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cities?.get(position)?.let { holder.bind(it) }

        holder.itemView.setOnClickListener {
            cities?.get(position)?.let { it1 -> cellClickListener.onCellClickListener(it1) }
        }
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return cities?.size ?: 0
    }

    inner class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val cityView: TextView = view.findViewById(R.id.item_city)
        private val cityLetterView: TextView = view.findViewById(R.id.item_cityLetter)
        private val countryView: TextView = view.findViewById(R.id.item_country)
        private val addToFavsView: Button = view.findViewById(R.id.addToFavs)
        var recyclerViewActivity = localContext as RecyclerViewActivity?


        fun bind(city: City) {
            cityView.text = city.city
            cityLetterView.text = city.city[0].toString()
            countryView.text = city.country

            val favouriteCities: LiveData<MutableList<City>>? =
                recyclerViewActivity?.getFavouriteList()

            addToFavsView.setOnClickListener {
                if (favouriteCities!!.value!!.contains(city)) {
                    Toast.makeText(
                        localContext,
                        "City ${city.city} already in favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        localContext,
                        "City ${city.city} added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    favouriteCities.value!!.add(city)
                    adapter.notifyDataSetChanged()
                }
            }

        }
    }
}
