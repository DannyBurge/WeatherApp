package com.example.weatherinvoltacourse21api

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

//Создаем класс CitiesRecyclerAdapter и наследуем его от класса RecyclerView.Adapter,
//добавляем на вход обработчик событий
class CitiesRecyclerAdapter(
    context: Context,
    private val cities: MutableList<City>,
//    var prefs: SharedPreferences,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<CitiesRecyclerAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.city_list_item, parent, false)
        return ViewHolder(view)
    }

    //Задаем значения для элемента списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val editor = prefs.edit()
        var favouriteCities: MutableList<City>


        holder.bind(cities[position])

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(cities[position])
        }

        holder.favouriteView.setOnClickListener {
//            editor.putString("favouriteCities").apply()
        }
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return cities.size
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val cityView: TextView = view.findViewById(R.id.item_city)
        private val countryView: TextView = view.findViewById(R.id.item_country)
        val favouriteView: CheckBox = view.findViewById(R.id.itemCheckBox)

        fun bind(city: City) {
            cityView.text = city.city
            countryView.text = city.country
        }
    }
}
