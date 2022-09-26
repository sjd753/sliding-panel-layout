package com.sjd.slidinglayout

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sjd.library.SlidingPanelLayout
import com.sjd.slidinglayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.slidingPanelLayout.setPanelSlideDirect(SlidingPanelLayout.DIRECTION_RIGHT_PANEL)
        binding.slidingPanelLayout.setPeakAt(percent = 33)
        binding.slidingPanelLayout.setCallBack(object : SlidingPanelLayout.CallBack {
            override fun onSlidingPanelStateChanged(state: Int) {
                Log.e("onViewPanelStateChanged: ", "panel state: $state")
            }

            override fun onSlidingPanelPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int
            ) {

            }

            override fun onViewDragStateChanged(state: Int) {

            }
        })

        binding.fab.setOnClickListener { view ->
            toggleNavigationPanelState()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun toggleNavigationPanelState() {
        val panelState = binding.slidingPanelLayout.panelState
        binding.slidingPanelLayout.panelState =
            if (panelState == SlidingPanelLayout.STATE_OPEN)
                SlidingPanelLayout.STATE_PEAK
            else SlidingPanelLayout.STATE_OPEN
    }
}