package com.example.paintracker.interfaces

import java.io.OutputStream
import java.time.LocalDate

interface IPdfPainReportBuilderService {
    fun init(reportTitle: String, reportPatientName: String)
    fun filter(from: LocalDate, to: LocalDate)
    fun generatePdf(outputStream: OutputStream)
}