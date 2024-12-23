package com.example.paintracker

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.VisualiserLayer
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.interfaces.Side
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate

class PainVisualiser @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val configService: IConfigService
    private val visualiserLayerIoService: IVisualiserLayerIoService

    var selectedDate: LocalDate = LocalDate.now()
        set(value) {
            field = value

            visualiserLayerIoService.loadAll(value, visualLayers, signaturePad.width, signaturePad.height)
            frontDrawing = selectedVisualiserLayer?.frontDrawing
            backDrawing = selectedVisualiserLayer?.backDrawing
            isDirty = false
            reflectIsDirty()

            switchDrawing()
        }
    private var painCategories: List<PainCategory> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                Log.i("VisualiserLayerIoService","Pain categories have changed, updating visual layers.")
                visualLayers.clear()
                value.forEach { category ->
                    visualLayers.add(VisualiserLayer(category, null, null))
                }

                selectedCategory = value[0]
                updateSelectedVisualLayer()
                updateCategoryButtonColor()
                updateDrawingColor()

                visualiserLayerIoService.loadAll(selectedDate, visualLayers, signaturePad.width, signaturePad.height)
                frontDrawing = selectedVisualiserLayer?.frontDrawing
                backDrawing = selectedVisualiserLayer?.backDrawing
                isDirty = false
                reflectIsDirty()

                switchDrawing()
            }
        }

    private var signaturePad: SignaturePad
    private var imageView: ImageView
    private var frontButton: Button
    private var backButton: Button
    private var categoryButton: FloatingActionButton
    private var saveButton: FloatingActionButton
    private var deleteButton: FloatingActionButton
    private var showAllButton: CheckBox
    private var isFront = true
    private var showAllLayers = false
    private var isDirty = false
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
        Log.i("VisualiserLayerIoService","Initializing PainVisualizer...")

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            HiltServiceBridge::class.java
        )
        configService = entryPoint.getConfigService()
        visualiserLayerIoService = entryPoint.getVisualiserLayerIoService()

        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.pain_visualiser, this, true)

        // Find views
        signaturePad = findViewById(R.id.signaturePad)
        imageView = findViewById(R.id.imageView)
        frontButton = findViewById(R.id.buttonFront)
        backButton = findViewById(R.id.buttonBack)
        categoryButton = findViewById(R.id.categoryButton)
        showAllButton = findViewById(R.id.showAllButton)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        // Set initial image
        imageView.setImageResource(frontImageRes)

        frontButton.setOnClickListener {
            checkAndSaveIfDirty {
                isFront = true
                imageView.setImageResource(frontImageRes)
                switchDrawing()
            }
        }

        backButton.setOnClickListener {
            checkAndSaveIfDirty {
                isFront = false
                imageView.setImageResource(backImageRes)
                switchDrawing()
            }
        }

        categoryButton.setOnClickListener { showCategoryDropdown() }

        showAllButton.setOnClickListener {
            checkAndSaveIfDirty {
                showAllLayers = showAllButton.isChecked
                val signaturePad = findViewById<SignaturePad>(R.id.signaturePad)
                signaturePad.isEnabled = !showAllLayers
                switchDrawing()
            }
        }

        saveButton.setOnClickListener {
            saveCurrentDrawing()
            isDirty = false
            reflectIsDirty()
        }

        deleteButton.setOnClickListener {
            if(!showAllLayers) { // Can't delete all layers yet
                visualiserLayerIoService.deleteLayer(selectedDate, selectedVisualiserLayer!!, if(isFront) Side.FRONT else Side.BACK)
                frontDrawing = selectedVisualiserLayer?.frontDrawing
                backDrawing = selectedVisualiserLayer?.backDrawing
                isDirty = false
                reflectIsDirty()
                switchDrawing()
            }
        }

        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onSigned() {
                //isDirty = true
                //reflectIsDirty()
            }

            override fun onClear() {
                isDirty = false
                reflectIsDirty()
            }

            override fun onStartSigning() {
                isDirty = true
                reflectIsDirty()
            }
        })

        painCategories = configService.getCurrent().painCategories
        Log.i("VisualiserLayerIoService","PainVisualizer initialized.")
    }

    private fun reflectIsDirty() {
        saveButton.visibility = if (isDirty) VISIBLE else INVISIBLE
    }

    private fun mergeAllLayers(): Bitmap {
        Log.i("VisualiserLayerIoService","Merging all layers.")
        val resultBitmap = Bitmap.createBitmap(signaturePad.width, signaturePad.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        for (visualLayer in visualLayers) {
            val drawing = if (isFront) visualLayer.frontDrawing else visualLayer.backDrawing
            drawing?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        }

        return resultBitmap
    }

    private fun checkAndSaveIfDirty(onCompleted: () -> Unit) {
        if (isDirty) {
            Log.i("VisualiserLayerIoService","Changes have not been saved, prompting to save.")
            AlertDialog.Builder(context)
                .setTitle("Unsaved changes")
                .setMessage("Do you want to save your changes?")
                .setPositiveButton("Yes") { _, _ ->
                    saveCurrentDrawing()
                    onCompleted() // Proceed after saving
                }
                .setNegativeButton("No") { _, _ ->
                    onCompleted() // Proceed without saving
                }
                .setOnCancelListener {
                    onCompleted() // Treat cancel as proceeding without saving
                }
                .show()
        } else {
            onCompleted() // If not dirty, proceed immediately
        }
    }

    private fun updateSelectedVisualLayer() {
        Log.i("VisualiserLayerIoService","Updating selected visual layer.")
        val curSelectedVisualiserLayer = visualLayers.find { it.painCategory == selectedCategory }
        if(curSelectedVisualiserLayer != null)
        {
            frontDrawing = curSelectedVisualiserLayer.frontDrawing
            backDrawing = curSelectedVisualiserLayer.backDrawing
            selectedVisualiserLayer = curSelectedVisualiserLayer
        }
    }

    private fun saveCurrentDrawing() {
        Log.i("VisualiserLayerIoService","Saving current visual layer.")
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

        visualiserLayerIoService.saveLayer(selectedDate, selectedVisualiserLayer!!, if(isFront) Side.FRONT else Side.BACK)
        deleteButton.visibility = VISIBLE
    }

    private fun switchDrawing() {
        Log.i("VisualiserLayerIoService","Switching to ${if (isFront) "front" else "back"}.")
        var drawingToShow = if (isFront) frontDrawing else backDrawing
        drawingToShow = if (showAllLayers) mergeAllLayers() else drawingToShow

        if (drawingToShow != null) {
            signaturePad.signatureBitmap = drawingToShow
            deleteButton.visibility = if(showAllLayers) INVISIBLE else VISIBLE
        } else {
            signaturePad.clear()
            deleteButton.visibility = INVISIBLE
        }
    }

    private fun updateCategoryButtonColor() {
        Log.i("VisualiserLayerIoService","Updating category button colour.")
        selectedCategory?.let {
            categoryButton.backgroundTintList = ColorStateList.valueOf(it.colour)
        }
    }

    private fun updateDrawingColor() {
        Log.i("VisualiserLayerIoService","Updating drawing colour.")
        selectedCategory?.let {
            signaturePad.setPenColor(it.colour)
        }
    }

    private fun showCategoryDropdown() {
        Log.i("VisualiserLayerIoService","Showing pain category dropdown.")
        val popupMenu = PopupMenu(context, categoryButton)
        painCategories.forEachIndexed { index, category ->
            val menuItem = popupMenu.menu.add(0, index, index, category.displayName)
            menuItem.isChecked = !showAllLayers && category == selectedCategory
            menuItem.isCheckable = true
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            Log.i("VisualiserLayerIoService","Selected pain category.")

            for (i in 0 until popupMenu.menu.size()) {
                popupMenu.menu.getItem(i).isChecked = false
            }

            checkAndSaveIfDirty {
                showAllLayers = false
                showAllButton.isChecked = showAllLayers
                signaturePad.isEnabled = !showAllLayers
                menuItem.isChecked = true
                val selectedIndex = menuItem.itemId
                selectedCategory = painCategories[selectedIndex]
                updateSelectedVisualLayer()
                updateCategoryButtonColor()
                updateDrawingColor()
                switchDrawing()
            }

            true
        }

        popupMenu.show()
    }
}
