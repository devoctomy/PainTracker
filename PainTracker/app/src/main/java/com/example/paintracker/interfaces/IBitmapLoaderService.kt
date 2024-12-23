package com.example.paintracker.interfaces

import android.graphics.Bitmap

interface IBitmapLoaderService {
    fun loadBitmap(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap?
}