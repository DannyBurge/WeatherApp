package com.example.weatherinvoltacourse21api

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Создаем класс CitiesRecyclerAdapter и наследуем его от класса RecyclerView.Adapter,
//добавляем на вход обработчик событий
class CitiesRecyclerAdapter(
    context: Context,
    private val cities: List<City>,
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
        holder.bind(cities[position])

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(cities[position])
        }
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return cities.size
    }

    fun setItems(cities: List<City>) {

    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val cityView: TextView = view.findViewById(R.id.item_city)
        private val countryView: TextView = view.findViewById(R.id.item_country)

        fun bind(city: City) {
            cityView.text = city.city
            countryView.text = city.country
        }
    }
}
