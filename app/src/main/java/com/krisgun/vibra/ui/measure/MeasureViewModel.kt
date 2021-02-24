package com.krisgun.vibra.ui.measure

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.krisgun.vibra.R

private const val TAG = "MeasureViewModel"

class MeasureViewModel : ViewModel() {

    private lateinit var navController: NavController

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun onStart() {
        Log.d(TAG, "Clicky!")
        navController.navigate(R.id.action_navigation_measure_to_navigation_collect_data)
    }
}