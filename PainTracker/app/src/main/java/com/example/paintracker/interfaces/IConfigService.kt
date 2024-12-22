package com.example.paintracker.interfaces

import android.content.Context
import com.example.paintracker.data.Config

interface IConfigService {
    fun initialize(context: Context)
    fun getCurrent(): Config
}