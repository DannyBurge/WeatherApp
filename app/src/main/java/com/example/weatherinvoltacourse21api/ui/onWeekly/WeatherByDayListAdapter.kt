package com.example.weatherinvoltacourse21api.ui.onWeekly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherinvoltacourse21api.R

// Создаем класс и наследуем его от класса RecyclerView.Adapter,
// добавляем на вход обработчик событий
class WeatherByDayListAdapter(
    context: Context,
    private val weatherByHour: MutableList<WeatherByDayForAdapter>
) :
    RecyclerView.Adapter<WeatherByDayListAdapter.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.day_weather_item, parent, false)
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
        private val dateDay: TextView = view.findViewById(R.id.dateDay)
        private val dayTemp: TextView = view.findViewById(R.id.mainTempDay)
        private val nightTemp: TextView = view.findViewById(R.id.mainTempNight)
        private val weatherMain: TextView = view.findViewById(R.id.dayWeatherInfoDescription)
        private val icon: ImageView = view.findViewById(R.id.dayWeatherInfoIcon)

        fun bind(weatherInfoByDay: WeatherByDayForAdapter) {
            dateDay.text = weatherInfoByDay.date
            dayTemp.text = weatherInfoByDay.dayTemp
            nightTemp.text = weatherInfoByDay.nightTemp
            weatherMain.text = weatherInfoByDay.weatherDescription
            // В зависимости от кода в сообщении рисуем нужную иконку
            when (weatherInfoByDay.idWeather / 100) {
                2 -> icon.setImageResource(R.mipmap.storm)
                3 -> icon.setImageResource(R.mipmap.wet)
                5 -> icon.setImageResource(R.mipmap.rain)
                6 -> icon.setImageResource(R.mipmap.snow)
                7 -> icon.setImageResource(R.mipmap.dry)
                8 -> when (weatherInfoByDay.idWeather % 100) {
                    0 -> icon.setImageResource(R.mipmap.sun)
                    1 -> icon.setImageResource(R.mipmap.sun_cloud)
                    2 -> icon.setImageResource(R.mipmap.cloud)
                    3 -> icon.setImageResource(R.mipmap.clouds)
                    4 -> icon.setImageResource(R.mipmap.clouds)
                }

            }
        }
    }
}