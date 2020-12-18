package com.example.weatherinvoltacourse21api.ui.onWeekly

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

class OnWeeklyFragment : Fragment() {

    private lateinit var onWeeklyViewModel: OnWeeklyViewModel

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
        onWeeklyViewModel = ViewModelProvider(this).get(OnWeeklyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_weekly, container, false)
        val textView: TextView = root.findViewById(R.id.text_onWeeklyFragment)
        onWeeklyViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}