package com.example.paintracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.ui.setupWithNavController
import com.example.paintracker.R
import com.example.paintracker.databinding.ActivityMainBinding
import com.example.paintracker.interfaces.IPainContext
import com.example.paintracker.interfaces.IPdfPainReportBuilderService
import com.example.paintracker.interfaces.IPdfPainReportBuilderServiceFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream
import java.time.LocalDate
import javax.inject.Inject
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Dependency Injection
    @Inject lateinit var painContext: IPainContext
    @Inject lateinit var pdfPainReportBuilderServiceFactory: IPdfPainReportBuilderServiceFactory


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("ViewBinding is only valid between onCreateView and onDestroyView.")
    private lateinit var savePdfLauncher: ActivityResultLauncher<String>

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var selectedDate: LocalDate = LocalDate.now()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PDFBoxResourceLoader.init(this) // Initialize PDFBox-Android

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savePdfLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/pdf")
        ) { uri ->
            if (uri != null) {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    generatePdf(pdfPainReportBuilderServiceFactory.create(), outputStream)
                }
            }
        }
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
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
            R.id.action_generate_pdf -> {
                savePdfLauncher.launch("Report.pdf")
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

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun generatePdf(
        pdfPainReportBuilderService: IPdfPainReportBuilderService,
        outputStream: OutputStream
    )
    {
        pdfPainReportBuilderService.init(this, "Pain Report", "John Doe")
        pdfPainReportBuilderService.filter(LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31))
        pdfPainReportBuilderService.generatePdf(outputStream)
    }

    private fun showDatePickerDialog() {
        val year = selectedDate.year
        val month = selectedDate.monthValue - 1
        val day = selectedDate.dayOfMonth

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                //val curSelectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                //Toast.makeText(this, "Selected Date: $curSelectedDate", Toast.LENGTH_SHORT).show()

                selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                painContext.selectedDate = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}