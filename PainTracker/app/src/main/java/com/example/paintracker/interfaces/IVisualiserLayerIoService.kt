package com.example.paintracker.interfaces

import android.graphics.Bitmap
import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.VisualiserLayer
import java.time.LocalDate

enum class Side {
    FRONT, BACK
}

interface IVisualiserLayerIoService {
    fun loadAll(localDate: LocalDate, layers: MutableList<VisualiserLayer>, width: Int, height: Int)
    fun loadLayerSideImage(localDate: LocalDate, layer: VisualiserLayer, side: Side, width: Int, height: Int) : Bitmap?
    fun saveLayer(localDate: LocalDate, layer: VisualiserLayer, side: Side)
    fun deleteLayer(localDate: LocalDate, layer: VisualiserLayer, side: Side)
    fun deleteLayers(localDate: LocalDate, layers: MutableList<VisualiserLayer>)
    fun layerDataExists(localDate: LocalDate, layer: VisualiserLayer): Boolean
    fun painCategoryDataExists(localDate: LocalDate, painCategory: PainCategory): Boolean
    fun notesExist(localDate: LocalDate): Boolean
}