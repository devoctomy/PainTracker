package com.example.paintracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PainTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize global resources or configurations here
    }
}