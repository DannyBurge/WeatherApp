package com.example.weatherinvoltacourse21api

import android.app.Activity
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

//Ключевое слово здесь - suspend. Это даст Котлину понять, что это suspend функция и она будет приостанавливать корутину.
suspend fun makeRequest(urlString: String): String {
    //выполняем необходимую работу в фоновом потоке
    return withContext(Dispatchers.IO) {
        var buffer: BufferedReader? = null
        try {
            //создаем экземпляр класса URL, передаем в него наш адрес
            val url = URL(urlString.replace(" ", "%20"))

//            val url = URL("https://api.openweathermap.org/data/2.5/weather?q=Moscow&appid=96130c474a1cba9f126dba69955d342e")
            //создаем экземпляр класса HttpsURLConnection
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            //указываем какой HTTP метод мы будем использовать
            httpsURLConnection.requestMethod = "GET"
            //указываем таймаут на чтение ответа от сервера
            httpsURLConnection.readTimeout = 10000
            //выполняем подключение к серверу
            httpsURLConnection.connect()

            //получаем входящий поток данных и помещаем его в наш буффер
            buffer = BufferedReader(InputStreamReader(httpsURLConnection.inputStream))

            val builder = StringBuilder()
            var line: String? = null

            while (true) {
                //читаем строку из буффера, если читать больше нечего прерываем цикл
                line = buffer.readLine() ?: break
                //добавляем нашу строку в StringBuilder
                builder.append(line).append("")
            }

            //возвращаем результат выполнения функции
            builder.toString()
        } catch (exc: Exception) {
            buffer?.close()
            exc.message.toString()
        } finally {
            //обязательно выгружаем буффер из памяти
            buffer?.close()
        }
    }
}