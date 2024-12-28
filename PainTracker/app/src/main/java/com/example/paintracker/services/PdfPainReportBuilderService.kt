package com.example.paintracker.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IPdfPainReportBuilderService
import java.time.LocalDate
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import com.example.paintracker.R
import com.example.paintracker.interfaces.IPathService
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.io.OutputStream
import kotlin.io.path.exists

class PdfPainReportBuilderService constructor(
    private val configService: IConfigService,
    private val pathService: IPathService,
    private val dataManagerService: IDataManagerService
)  : IPdfPainReportBuilderService {

    private var painEntries: List<PainEntry>? = null
    private var rangedPainEntries: List<PainEntry>? = null
    private var context: Context? = null
    private var title: String = "Pain Report"
    private var patientName: String = "John Doe"

    override fun init(appContext: Context, reportTitle: String, reportPatientName: String) {
        context = appContext
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
            val coverPage = PDPage(PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width))
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

            val frontBody = getBitmapFromResource(R.drawable.body_front)
            val backBody = getBitmapFromResource(R.drawable.body_back)

            for (painEntry in rangedPainEntries!!) {
                val frontImages = mutableListOf<Bitmap>()
                val backImages = mutableListOf<Bitmap>()
                for (painCategory in painEntry.painCategories) {
                    val pathCategoryPath = pathService.getPainCategoryPath(painEntry.date, painCategory)

                    val frontPath = pathCategoryPath.resolve("front.png")
                    if(frontPath.exists()) {
                        val frontBitmap = BitmapFactory.decodeFile(frontPath.toString())
                        frontImages.add(frontBitmap)
                    }

                    val backPath = pathCategoryPath.resolve("back.png")
                    if(backPath.exists()) {
                        val backBitmap = BitmapFactory.decodeFile(backPath.toString())
                        backImages.add(backBitmap)
                    }
                }

                var frontSize: Pair<Int, Int>? = null
                if (frontImages.isNotEmpty()) {
                    frontSize = frontImages[0].width to frontImages[0].height
                    if (!frontImages.all { it.width to it.height == frontSize }) {
                        throw IllegalArgumentException("All front images must have the same size.")
                    }
                }

                var frontBitmap : Bitmap? = null
                if (frontSize != null) {
                    // Draw body background
                    val width = frontSize.first
                    val height = frontSize.second
                    frontBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(frontBitmap)
                    val scale = minOf(width.toFloat() / frontBody.width, height.toFloat() / frontBody.height)
                    val scaledWidth = (frontBody.width * scale).toInt()
                    val scaledHeight = (frontBody.height * scale).toInt()
                    val left = (width - scaledWidth) / 2
                    val top = (height - scaledHeight) / 2
                    val matrix = Matrix().apply {
                        setScale(scale, scale)
                        postTranslate(left.toFloat(), top.toFloat())
                    }
                    canvas.drawColor(Color.WHITE)
                    canvas.drawBitmap(frontBody, matrix, Paint(Paint.ANTI_ALIAS_FLAG))

                    // Draw each front image
                    for (frontImage in frontImages)
                    {
                        canvas.drawBitmap(frontImage, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
                    }
                }

                var backSize: Pair<Int, Int>? = null
                if (backImages.isNotEmpty()) {
                    backSize = backImages[0].width to backImages[0].height
                    if (!backImages.all { it.width to it.height == backSize }) {
                        throw IllegalArgumentException("All back images must have the same size.")
                    }
                }

                var backBitmap : Bitmap? = null
                if(backSize != null)
                {
                    // Draw body background
                    val width = backSize.first
                    val height = backSize.second
                    backBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(backBitmap)
                    val scale = minOf(width.toFloat() / backBody.width, height.toFloat() / backBody.height)
                    val scaledWidth = (backBody.width * scale).toInt()
                    val scaledHeight = (backBody.height * scale).toInt()
                    val left = (width - scaledWidth) / 2
                    val top = (height - scaledHeight) / 2
                    val matrix = Matrix().apply {
                        setScale(scale, scale)
                        postTranslate(left.toFloat(), top.toFloat())
                    }
                    canvas.drawColor(Color.WHITE)
                    canvas.drawBitmap(backBody, matrix, Paint(Paint.ANTI_ALIAS_FLAG))

                    // Draw each front image
                    for (backImage in backImages)
                    {
                        canvas.drawBitmap(backImage, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
                    }
                }

                // skip if we have no front image for the moment
                if(frontBitmap == null) {
                    continue
                }

                // just draw the front image for now
                val currentPainPage = PDPage(PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width))
                document.addPage(currentPainPage)
                PDPageContentStream(document, currentPainPage).use { contentStream ->
                    val pdImage = LosslessFactory.createFromImage(document, frontBitmap)
                    val pageWidth = currentPainPage.mediaBox.width
                    val pageHeight = currentPainPage.mediaBox.height
                    val bitmapWidth = frontBitmap.width.toFloat()
                    val bitmapHeight = frontBitmap.height.toFloat()
                    val scale = minOf(pageWidth / bitmapWidth, pageHeight / bitmapHeight)
                    val scaledWidth = bitmapWidth * scale
                    val scaledHeight = bitmapHeight * scale
                    val xPosition = (pageWidth - scaledWidth) / 2
                    val yPosition = (pageHeight - scaledHeight) / 2

                    contentStream.drawImage(pdImage, xPosition, yPosition, scaledWidth, scaledHeight)
                }
            }

            document.save(outputStream)
        }
    }

    private fun getBitmapFromResource(resource: Int) : Bitmap {
        val drawable: Drawable? = AppCompatResources.getDrawable(context!!, resource)
        val bitmap: Bitmap? = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            // Create a bitmap if the drawable is not a BitmapDrawable
            val width = drawable?.intrinsicWidth ?: 0
            val height = drawable?.intrinsicHeight ?: 0
            val config = Bitmap.Config.ARGB_8888
            val bitmap = Bitmap.createBitmap(width, height, config)
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0, 0, canvas.width, canvas.height)
            drawable?.draw(canvas)
            bitmap
        }

        return bitmap!!
    }
}