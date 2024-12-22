package com.example.paintracker

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.paintracker.databinding.ActivityMainBinding
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
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
            R.id.action_select_date -> {
                showDatePickerDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun showDatePickerDialog() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
        var painVisualiser: PainVisualiser? = null
        if (currentFragment is RecordPainFragment) {
            painVisualiser = currentFragment.painVisualiser
        }

        if(painVisualiser == null) {
            Toast.makeText(this, "Pain visualiser not found", Toast.LENGTH_SHORT).show()
            return
        }

        val year = painVisualiser.selectedDate.year //calendar.get(Calendar.YEAR)
        val month = painVisualiser.selectedDate.monthValue - 1 //calendar.get(Calendar.MONTH)
        val day = painVisualiser.selectedDate.dayOfMonth //calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the selected date
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()

                painVisualiser.selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}