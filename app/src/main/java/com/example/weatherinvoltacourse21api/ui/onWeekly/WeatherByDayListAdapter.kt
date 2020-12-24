package com.example.weatherinvoltacourse21api.ui.onWeekly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherinvoltacourse21api.R

class WeatherByDayListAdapter(
    context: Context,
    private val weatherByHour: MutableList<WeatherByDay>
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
        private val time: TextView = view.findViewById(R.id.weatherInfoDate)
        private val dayTemp: TextView = view.findViewById(R.id.weatherInfoTempDay)
        private val nightTemp: TextView = view.findViewById(R.id.weatherInfoTempNight)
        private val weatherMain: TextView = view.findViewById(R.id.weatherInfoDescription)
        private val pressure: TextView = view.findViewById(R.id.weatherInfoPressure)
        private val humidity: TextView = view.findViewById(R.id.weatherInfoHumidity)
        private val windSpeed: TextView = view.findViewById(R.id.weatherInfoWindSpeed)

        fun bind(weatherInfoByHour: WeatherByDay) {
            time.text = weatherInfoByHour.time
            dayTemp.text = weatherInfoByHour.dayTemp
            nightTemp.text = weatherInfoByHour.nightTemp
            weatherMain.text = weatherInfoByHour.weatherMain
            pressure.text = weatherInfoByHour.pressure
            humidity.text = weatherInfoByHour.humidity
            windSpeed.text = weatherInfoByHour.wind_speed
        }
    }
}