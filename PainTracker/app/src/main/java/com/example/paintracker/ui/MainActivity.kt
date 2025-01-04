package com.example.paintracker.ui

import DataExporterService
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.ui.setupWithNavController
import com.example.paintracker.R
import com.example.paintracker.databinding.ActivityMainBinding
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IPainContext
import com.example.paintracker.interfaces.IPdfPainReportBuilderService
import com.example.paintracker.interfaces.IPdfPainReportBuilderServiceFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream
import java.time.LocalDate
import javax.inject.Inject
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Dependency Injection
    @Inject lateinit var configService: IConfigService
    @Inject lateinit var painContext: IPainContext
    @Inject lateinit var pdfPainReportBuilderServiceFactory: IPdfPainReportBuilderServiceFactory


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("ViewBinding is only valid between onCreateView and onDestroyView.")
    private lateinit var savePdfLauncher: ActivityResultLauncher<String>
    private lateinit var saveZipLauncher: ActivityResultLauncher<String>

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var selectedDate: LocalDate = LocalDate.now()
    private var from: LocalDate? = null
    private var to: LocalDate? = null

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

        saveZipLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
            if (uri != null) {
                exportData(uri)
            } else {
                Toast.makeText(this, "Export canceled", Toast.LENGTH_SHORT).show()
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
        return when (item.itemId) {
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.SettingsFragment)
                true
            }
            R.id.action_select_date -> {
                showDatePickerDialog()
                true
            }
            R.id.action_generate_pdf -> {
                showDateRangeDialog()
                true
            }
            R.id.action_export_data -> {
                val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val currentDate = dateFormat.format(System.currentTimeMillis())
                val defaultFileName = "PainTracker_Export_${currentDate}.zip"
                saveZipLauncher.launch(defaultFileName)
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

    private fun exportData(uri: Uri) {
        val exporter = DataExporterService()
        CoroutineScope(Dispatchers.IO).launch {
            val success = exporter.exportDataToZip(uri, applicationContext)
            runOnUiThread {
                if (success) {
                    Toast.makeText(this@MainActivity, "Data exported successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to export data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generatePdf(
        pdfPainReportBuilderService: IPdfPainReportBuilderService,
        outputStream: OutputStream
    )
    {
        pdfPainReportBuilderService.init(this, "Pain Report", configService.getCurrent().patientName)
        pdfPainReportBuilderService.filter(from!!, to!!)
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

    private fun showDateRangeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_range_picker, null)
        val fromDateEditText = dialogView.findViewById<EditText>(R.id.et_from_date)
        val toDateEditText = dialogView.findViewById<EditText>(R.id.et_to_date)
        val changeFromDateButton = dialogView.findViewById<AppCompatImageButton>(R.id.btn_change_from_date)
        val changeToDateButton = dialogView.findViewById<AppCompatImageButton>(R.id.btn_change_to_date)
        val saveButton = dialogView.findViewById<Button>(R.id.btn_save)

        var fromDate: LocalDate? = null
        var toDate: LocalDate? = null

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val updateEditText = { editText: EditText, date: LocalDate ->
            editText.setText(date.toString())
        }

        changeFromDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    fromDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateEditText(fromDateEditText, fromDate!!)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        changeToDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    toDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateEditText(toDateEditText, toDate!!)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        saveButton.setOnClickListener {
            if (fromDate == null || toDate == null) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fromDate!! > toDate!!) {
                Toast.makeText(this, "From date cannot be greater than To date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dialog.dismiss()
            from = fromDate
            to = toDate
            savePdfLauncher.launch("PainReport_${fromDate}_to_${toDate}.pdf")
        }

        dialog.show()
    }
}