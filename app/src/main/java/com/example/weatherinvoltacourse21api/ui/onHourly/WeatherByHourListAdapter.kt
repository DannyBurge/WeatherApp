package com.example.weatherinvoltacourse21api.ui.onHourly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        private val timeHourlyView: TextView = view.findViewById(R.id.dateDay)
        private val mainTempHourlyView: TextView = view.findViewById(R.id.mainTempDay)
        private val feelsLikeTempHourlyView: TextView = view.findViewById(R.id.mainTempNight)
        private val mainWeatherHourlyView: ImageView = view.findViewById(R.id.dayWeatherInfoIcon)
        private val hourWeatherInfoDescription: TextView = view.findViewById(R.id.dayWeatherInfoDescription)

        fun bind(weatherInfoByHour: WeatherByHour) {
            timeHourlyView.text = weatherInfoByHour.time
            mainTempHourlyView.text = weatherInfoByHour.mainTemp
            feelsLikeTempHourlyView.text = weatherInfoByHour.feelsLikeTemp
            hourWeatherInfoDescription.text = weatherInfoByHour.description
            when(weatherInfoByHour.idWeather[0]) {
                '2' -> mainWeatherHourlyView.setImageResource(R.mipmap.storm)
                '3' -> mainWeatherHourlyView.setImageResource(R.mipmap.wet)
                '5' -> mainWeatherHourlyView.setImageResource(R.mipmap.rain)
                '6' -> mainWeatherHourlyView.setImageResource(R.mipmap.snow)
                '7' -> mainWeatherHourlyView.setImageResource(R.mipmap.dry)
                '8' -> when(weatherInfoByHour.idWeather[2]) {
                    '0' -> mainWeatherHourlyView.setImageResource(R.mipmap.sun)
                    '1' -> mainWeatherHourlyView.setImageResource(R.mipmap.sun_cloud)
                    '2' -> mainWeatherHourlyView.setImageResource(R.mipmap.cloud)
                    '3' -> mainWeatherHourlyView.setImageResource(R.mipmap.clouds)
                    '4' -> mainWeatherHourlyView.setImageResource(R.mipmap.clouds)
                }

            }

        }
    }
}