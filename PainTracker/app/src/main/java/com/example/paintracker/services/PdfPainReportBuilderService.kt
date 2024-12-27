package com.example.paintracker.services

import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IPdfPainReportBuilderService
import java.time.LocalDate
import javax.inject.Inject
import android.util.Log

class PdfPainReportBuilderService @Inject constructor(
    private val configService: IConfigService,
    private val dataManagerService: IDataManagerService
)  : IPdfPainReportBuilderService {

    private var painEntries: List<PainEntry>? = null
    private var rangedPainEntries: List<PainEntry>? = null
    private var title: String = ""
    private var patientName: String = ""

    fun init(reportTitle: String, reportPatientName: String) {
        title = reportTitle
        patientName = reportPatientName
        painEntries = dataManagerService.listAllPainEntries(configService.getCurrent().painCategories)
        Log.i("PdfPainReportBuilderService", "Initialized '${title}' report for patient '${patientName}' with ${painEntries?.size} total pain entries.")
    }

    fun filter(from: LocalDate, to: LocalDate) {
        rangedPainEntries = painEntries?.filter { it.date >= from && it.date <= to }
        Log.i("PdfPainReportBuilderService", "Filtered out ${rangedPainEntries?.size} pain entries from ${from} to ${to}.")
    }
}