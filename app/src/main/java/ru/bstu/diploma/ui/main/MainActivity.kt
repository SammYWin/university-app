package ru.bstu.diploma.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.bstu.diploma.R
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.utils.FirestoreUtil
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("M_MainActivity", "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.chatListFragment,
            R.id.timetableFragment,
            R.id.profileFragment
        ).build()

        bottomNavView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    override fun onStart() {
        val user = User(isOnline = true)
        FirestoreUtil.updateCurrentUser(user)

        Log.d("M_MainActivity", "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("M_MainActivity", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("M_MainActivity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("M_MainActivity", "onStop")

        val user = User(lastVisit = Date(), isOnline = false)
        FirestoreUtil.updateCurrentUser(user)

        super.onStop()
    }

    override fun onDestroy() {
        Log.d("M_MainActivity", "onDestroy")

        super.onDestroy()
    }
}