package com.example.weatherinvoltacourse21api.ui.onHourly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherinvoltacourse21api.R

class WeatherByHourListAdapter(
    context: Context,
    private val weatherByHour: MutableList<WeatherByHour>
) :
    RecyclerView.Adapter<WeatherByHourListAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.hour_weather_item, parent, false)
        return ViewHolder(view)
    }

    //Задаем значения для элемента списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(weatherByHour[position])
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return weatherByHour.size
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val timeHourlyView: TextView = view.findViewById(R.id.timeHourly)
        private val mainTempHourlyView: TextView = view.findViewById(R.id.mainTempHourly)
        private val mainWeatherHourlyView: TextView = view.findViewById(R.id.mainWeatherHourly)

        fun bind(weatherInfoByHour: WeatherByHour) {
            timeHourlyView.text = weatherInfoByHour.time
            mainTempHourlyView.text = weatherInfoByHour.mainTemp
            mainWeatherHourlyView.text = weatherInfoByHour.mainWeather
        }
    }
}