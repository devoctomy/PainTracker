package com.example.paintracker

import android.graphics.Bitmap

data class VisualiserLayer (
    var painCategory: PainCategory?,
    var frontDrawing: Bitmap?,
    var backDrawing: Bitmap?
) {
}