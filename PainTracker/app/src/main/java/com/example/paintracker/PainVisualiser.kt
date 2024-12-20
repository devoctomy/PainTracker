package com.example.paintracker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
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
                selectedCategory = value[0]
                updateCategoryButtonColor()
                updateDrawingColor()
            }
        }

    private var imageView: ImageView
    private var frontButton: Button
    private var backButton: Button
    private var categoryButton: Button
    private var isFrontImage = true
    private var selectedCategory: PainCategory? = null

    // Images for front and back
    private val frontImageRes = R.drawable.body_front // Replace with your front image
    private val backImageRes = R.drawable.body_back   // Replace with your back image

    // Storage for drawing data
    private var frontDrawing: Bitmap? = null
    private var backDrawing: Bitmap? = null

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.pain_visualiser, this, true)

        // Find views
        imageView = findViewById(R.id.imageView)
        frontButton = findViewById(R.id.buttonFront)
        backButton = findViewById(R.id.buttonBack)
        categoryButton = findViewById(R.id.categoryButton)

        // Set initial image
        imageView.setImageResource(frontImageRes)

        // Button click listeners
        frontButton.setOnClickListener {
            saveCurrentDrawing() // Save current canvas
            isFrontImage = true
            imageView.setImageResource(frontImageRes)
            loadDrawing() // Load front canvas
        }

        backButton.setOnClickListener {
            saveCurrentDrawing() // Save current canvas
            isFrontImage = false
            imageView.setImageResource(backImageRes)
            loadDrawing() // Load back canvas
        }

        categoryButton.setOnClickListener { showCategoryDropdown() }
    }

    // Placeholder functions (we'll implement these in the next steps)
    private fun saveCurrentDrawing() {
        // Ensure the SignaturePad is available
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        // Create a transparent Bitmap with the same size as the SignaturePad
        val bitmap = Bitmap.createBitmap(
            signaturePad.width,
            signaturePad.height,
            Bitmap.Config.ARGB_8888 // Supports transparency
        )
        val canvas = Canvas(bitmap)

        // Draw the SignaturePad's content onto the transparent Bitmap
        signaturePad.draw(canvas)

        // Save the bitmap to the appropriate storage (front or back)
        if (isFrontImage) {
            frontDrawing = bitmap
        } else {
            backDrawing = bitmap
        }

        // Clear the SignaturePad for the next drawing
        signaturePad.clear()
    }

    private fun loadDrawing() {
        // Ensure the SignaturePad is available
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        // Load the appropriate drawing based on the current side
        val drawingToLoad = if (isFrontImage) frontDrawing else backDrawing

        if (drawingToLoad != null) {
            // Load the saved bitmap into the SignaturePad
            signaturePad.signatureBitmap = drawingToLoad
        } else {
            // Clear the pad if there's no saved drawing for the side
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
            val selectedIndex = menuItem.itemId
            selectedCategory = painCategories[selectedIndex]
            updateCategoryButtonColor()
            updateDrawingColor()
            true
        }

        popupMenu.show()
    }
}
