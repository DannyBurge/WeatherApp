package com.example.weatherinvoltacourse21api.ui.onHourly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherinvoltacourse21api.R
import timber.log.Timber

class OnHourlyFragment : Fragment() {

    private lateinit var onHourlyViewModel: OnHourlyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Timber.i("Fragment onCreate")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        onHourlyViewModel =
            ViewModelProvider(this).get(OnHourlyViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_hourly, container, false)
        val textView: TextView = root.findViewById(R.id.text_onHourlyFragment)
        onHourlyViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}