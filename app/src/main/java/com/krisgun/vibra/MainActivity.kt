package com.krisgun.vibra

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_measure_countdown, R.id.navigation_history, R.id.navigation_liveview))

        bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return if (navController.previousBackStackEntry?.destination?.id  == R.id.navigation_collect_data) {
            navController.popBackStack(R.id.navigation_measure_countdown, false)
        } else
            navController.popBackStack()
    }


}