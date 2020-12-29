package com.example.weatherinvoltacourse21api

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

//Создаем класс CitiesRecyclerAdapter и наследуем его от класса RecyclerView.Adapter,
//добавляем на вход обработчик событий
class FavouriteCitiesRecyclerAdapter(
    context: Context,
    private val favouriteCities: MutableLiveData<MutableList<City>>,
//    var prefs: SharedPreferences,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<FavouriteCitiesRecyclerAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private val localContext = context

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.city_list_favourite_item, parent, false)
        return ViewHolder(view)
    }

    //Задаем значения для элемента списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(favouriteCities.value!![position])

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(favouriteCities.value!![position])
        }
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return favouriteCities.value!!.size
    }

    inner class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val cityView: TextView = view.findViewById(R.id.item_city)
        private val cityLetterView: TextView = view.findViewById(R.id.item_cityLetter)
        private val countryView: TextView = view.findViewById(R.id.item_country)
        private val removeView: Button = view.findViewById(R.id.removeItem)
        private val favouriteItem: CheckBox = view.findViewById(R.id.favouriteItem)

        var recyclerViewActivity = localContext as RecyclerViewActivity?

        fun bind(city: City) {
            cityView.text = city.city
            cityLetterView.text = city.city[0].toString()
            countryView.text = city.country
            favouriteItem.isChecked =  city.isFavourite

            val favouriteCities: LiveData<MutableList<City>>? =
                recyclerViewActivity?.getFavouriteList()

            favouriteItem.setOnClickListener {
                if (favouriteItem.isChecked) {
                    for (cityInFav in favouriteCities!!.value!!) {
                        cityInFav.isFavourite = false
                    }
                    city.isFavourite = true
                } else {
                    favouriteCities!!.value!![0].isFavourite = true
                    if (city.city != "Auto") city.isFavourite = false
                }
                this@FavouriteCitiesRecyclerAdapter.notifyDataSetChanged()
            }

            if (city.city != "Auto") {
                removeView.setOnClickListener {
                    Toast.makeText(
                        localContext,
                        "City ${city.city} removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (city.isFavourite) {
                        favouriteCities!!.value!![0].isFavourite = true
                    }
                    favouriteCities!!.value!!.remove(city)
                    this@FavouriteCitiesRecyclerAdapter.notifyDataSetChanged()
                }
            } else removeView.visibility = View.INVISIBLE
        }
    }
}
