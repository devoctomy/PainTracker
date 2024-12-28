package com.example.paintracker.interfaces

import android.content.Context
import java.io.OutputStream
import java.time.LocalDate

interface IPdfPainReportBuilderService {
    fun init(appContext: Context, reportTitle: String, reportPatientName: String)
    fun filter(from: LocalDate, to: LocalDate)
    fun generatePdf(outputStream: OutputStream)
}