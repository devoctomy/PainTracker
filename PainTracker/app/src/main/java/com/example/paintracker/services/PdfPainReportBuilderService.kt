package com.example.paintracker.services

import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IPdfPainReportBuilderService
import java.time.LocalDate
import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.OutputStream

class PdfPainReportBuilderService constructor(
    private val configService: IConfigService,
    private val dataManagerService: IDataManagerService
)  : IPdfPainReportBuilderService {

    private var painEntries: List<PainEntry>? = null
    private var rangedPainEntries: List<PainEntry>? = null
    private var title: String = "Pain Report"
    private var patientName: String = "John Doe"

    override fun init(reportTitle: String, reportPatientName: String) {
        title = reportTitle
        patientName = reportPatientName
        painEntries = dataManagerService.listAllPainEntries(configService.getCurrent().painCategories)
        Log.i("PdfPainReportBuilderService", "Initialized '${title}' report for patient '${patientName}' with ${painEntries?.size} total pain entries.")
    }

    override fun filter(from: LocalDate, to: LocalDate) {
        rangedPainEntries = painEntries?.filter { it.date >= from && it.date <= to }
        Log.i("PdfPainReportBuilderService", "Filtered out ${rangedPainEntries?.size} pain entries from ${from} to ${to}.")
    }

    override fun generatePdf(outputStream: OutputStream) {
        PDDocument().use { document ->
            val coverPage = PDPage(PDRectangle.A4)
            document.addPage(coverPage)

            PDPageContentStream(document, coverPage).use { contentStream ->
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 36f)
                contentStream.beginText()
                contentStream.newLineAtOffset(100f, coverPage.mediaBox.height - 100f)
                contentStream.showText(title)
                contentStream.endText()

                contentStream.setFont(PDType1Font.HELVETICA, 24f)
                contentStream.beginText()
                contentStream.newLineAtOffset(100f, coverPage.mediaBox.height - 150f)
                contentStream.showText(patientName)
                contentStream.endText()
            }

            document.save(outputStream)
        }
    }
}