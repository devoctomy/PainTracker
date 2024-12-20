package com.example.paintracker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu

class PainVisualiser @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var painCategories: List<PainCategory> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                visualLayers.clear()
                value.forEach { category ->
                    visualLayers.add(VisualiserLayer(category, null, null))
                }

                selectedCategory = value[0]
                updateSelectedVisualLayer()
                updateCategoryButtonColor()
                updateDrawingColor()
            }
        }

    private var imageView: ImageView
    private var frontButton: Button
    private var backButton: Button
    private var categoryButton: Button
    private var showAllButton: CheckBox
    private var isFront = true
    private var showAllLayers = false
    private var selectedCategory: PainCategory? = null
    private var selectedVisualiserLayer: VisualiserLayer? = null

    // Images for front and back
    private val frontImageRes = R.drawable.body_front
    private val backImageRes = R.drawable.body_back

    // Storage for drawing data
    private var frontDrawing: Bitmap? = null
    private var backDrawing: Bitmap? = null

    private var visualLayers: MutableList<VisualiserLayer> = mutableListOf()

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.pain_visualiser, this, true)

        // Find views
        imageView = findViewById(R.id.imageView)
        frontButton = findViewById(R.id.buttonFront)
        backButton = findViewById(R.id.buttonBack)
        categoryButton = findViewById(R.id.categoryButton)
        showAllButton = findViewById(R.id.showAllButton)

        // Set initial image
        imageView.setImageResource(frontImageRes)

        frontButton.setOnClickListener {
            saveCurrentDrawing()
            isFront = true
            imageView.setImageResource(frontImageRes)
            loadDrawing()
        }

        backButton.setOnClickListener {
            saveCurrentDrawing()
            isFront = false
            imageView.setImageResource(backImageRes)
            loadDrawing()
        }

        categoryButton.setOnClickListener { showCategoryDropdown() }

        showAllButton.setOnClickListener {
            if(!showAllLayers)
            {
                saveCurrentDrawing()
            }
            showAllLayers = showAllButton.isChecked
            val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)
            signaturePad.isEnabled = !showAllLayers
            loadDrawing()
        }
    }

    private fun mergeAll(): Bitmap {
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        val width = signaturePad.width
        val height = signaturePad.height

        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        for (visualLayer in visualLayers) {
            val drawing = if (isFront) visualLayer.frontDrawing else visualLayer.backDrawing
            drawing?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        }

        return resultBitmap
    }

    private fun updateSelectedVisualLayer() {
        val curSelectedVisualiserLayer = visualLayers.find { it.painCategory == selectedCategory }
        if(curSelectedVisualiserLayer != null)
        {
            frontDrawing = curSelectedVisualiserLayer.frontDrawing
            backDrawing = curSelectedVisualiserLayer.backDrawing
            selectedVisualiserLayer = curSelectedVisualiserLayer
        }
    }

    private fun saveCurrentDrawing() {
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        val bitmap = Bitmap.createBitmap(
            signaturePad.width,
            signaturePad.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        signaturePad.draw(canvas)

        if (isFront) {
            frontDrawing = bitmap
            selectedVisualiserLayer?.frontDrawing = frontDrawing
        } else {
            backDrawing = bitmap
            selectedVisualiserLayer?.backDrawing = backDrawing
        }

        signaturePad.clear()
    }

    private fun loadDrawing() {
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        var drawingToLoad = if (isFront) frontDrawing else backDrawing
        drawingToLoad = if (showAllLayers) mergeAll() else drawingToLoad

        if (drawingToLoad != null) {
            signaturePad.signatureBitmap = drawingToLoad
        } else {
            signaturePad.clear()
        }
    }

    private fun updateCategoryButtonColor() {
        selectedCategory?.let {
            categoryButton.backgroundTintList = ColorStateList.valueOf(it.colour)
        }
    }

    private fun updateDrawingColor() {
        selectedCategory?.let {
            val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)
            signaturePad.setPenColor(it.colour)
        }
    }

    private fun showCategoryDropdown() {
        val popupMenu = PopupMenu(context, categoryButton)
        painCategories.forEachIndexed { index, category ->
            popupMenu.menu.add(0, index, index, category.displayName)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            if(!showAllLayers)
            {
                saveCurrentDrawing()
            }
            showAllLayers = false
            showAllButton.isChecked = showAllLayers
            val selectedIndex = menuItem.itemId
            selectedCategory = painCategories[selectedIndex]
            updateSelectedVisualLayer()
            updateCategoryButtonColor()
            updateDrawingColor()
            loadDrawing()
            true
        }

        popupMenu.show()
    }
}
