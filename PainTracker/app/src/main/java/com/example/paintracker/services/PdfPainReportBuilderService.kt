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
import com.example.paintracker.interfaces.INotesIoService
import com.example.paintracker.interfaces.IPathService
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.io.OutputStream
import java.time.format.DateTimeFormatter
import java.util.StringTokenizer
import kotlin.io.path.exists

class PdfPainReportBuilderService constructor(
    private val configService: IConfigService,
    private val pathService: IPathService,
    private val dataManagerService: IDataManagerService,
    private val notesIoService: INotesIoService
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

                // Load each front and back image, add to the relevant list
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

                // Get size of front images
                var frontSize: Pair<Int, Int>? = null
                if (frontImages.isNotEmpty()) {
                    frontSize = frontImages[0].width to frontImages[0].height
                    if (!frontImages.all { it.width to it.height == frontSize }) {
                        throw IllegalArgumentException("All front images must have the same size.")
                    }
                }

                // Create our front body image with pain layers
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

                // Get size of back images
                var backSize: Pair<Int, Int>? = null
                if (backImages.isNotEmpty()) {
                    backSize = backImages[0].width to backImages[0].height
                    if (!backImages.all { it.width to it.height == backSize }) {
                        throw IllegalArgumentException("All back images must have the same size.")
                    }
                }

                // Create our back body image with pain layers
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

                // Draw the pain entry page
                val currentPainPage = PDPage(PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width))
                document.addPage(currentPainPage)
                PDPageContentStream(document, currentPainPage).use { contentStream ->
                    val pageWidth = currentPainPage.mediaBox.width
                    val pageHeight = currentPainPage.mediaBox.height

                    val widthThird = pageWidth / 3

                    // Draw the date
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24f)
                    contentStream.beginText()
                    contentStream.newLineAtOffset(64f, coverPage.mediaBox.height - 64f)
                    contentStream.showText(formatPainEntryDate(painEntry.date))
                    contentStream.endText()

                    if(painEntry.hasNotes)
                    {
                        val notes = notesIoService.loadNotes(painEntry.date)
                        drawTextWrapped(
                            contentStream = contentStream,
                            text = notes!!,
                            x = 64f,
                            y = pageHeight - 150f,
                            width = widthThird - 64f,
                            font = PDType1Font.HELVETICA,
                            fontSize = 12f,
                            lineSpacing = 14f
                        )
                    }

                    // Draw front image
                    if (frontBitmap != null) {
                        val bitmapWidth = frontBitmap.width.toFloat()
                        val bitmapHeight = frontBitmap.height.toFloat()
                        val scale = minOf(widthThird / bitmapWidth, pageHeight / bitmapHeight)
                        val scaledWidth = bitmapWidth * scale
                        val scaledHeight = bitmapHeight * scale
                        val yPosition = (pageHeight - scaledHeight) / 2
                        val pdImage = LosslessFactory.createFromImage(document, frontBitmap)
                        contentStream.drawImage(pdImage, widthThird, yPosition, scaledWidth, scaledHeight)
                    }

                    // Draw back image
                    if (backBitmap != null) {
                        val bitmapWidth = backBitmap.width.toFloat()
                        val bitmapHeight = backBitmap.height.toFloat()
                        val scale = minOf(widthThird / bitmapWidth, pageHeight / bitmapHeight)
                        val scaledWidth = bitmapWidth * scale
                        val scaledHeight = bitmapHeight * scale
                        val yPosition = (pageHeight - scaledHeight) / 2
                        val pdImage = LosslessFactory.createFromImage(document, backBitmap)
                        contentStream.drawImage(pdImage, widthThird * 2, yPosition, scaledWidth, scaledHeight)
                    }
               }
            }

            document.save(outputStream)
        }
    }

    fun drawTextWrapped(contentStream: PDPageContentStream, text: String, x: Float, y: Float, width: Float, font: PDFont, fontSize: Float, lineSpacing: Float) {
        contentStream.setFont(font, fontSize)
        contentStream.beginText()
        contentStream.newLineAtOffset(x, y)

        val tokenizer = StringTokenizer(text)
        val line = StringBuilder()
        var currentLineHeight = y
        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()
            val lineWithToken = if (line.isEmpty()) token else "$line $token"
            val lineWidth = font.getStringWidth(lineWithToken) / 1000 * fontSize
            if (lineWidth > width) {
                contentStream.showText(line.toString())
                contentStream.newLineAtOffset(0f, -lineSpacing)
                currentLineHeight -= lineSpacing
                line.clear()
                line.append(token)
            } else {
                line.append(" $token")
            }
        }
        if (line.isNotEmpty()) {
            contentStream.showText(line.toString())
        }
        contentStream.endText()
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

    private fun formatPainEntryDate(date: LocalDate): String {
        val day = date.dayOfMonth
        val ordinal = getOrdinal(day)
        val monthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        return "$day$ordinal $monthYear"
    }

    private fun getOrdinal(day: Int): String {
        return when {
            day % 100 in 11..13 -> "th" // Special case for 11th, 12th, 13th
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }
}