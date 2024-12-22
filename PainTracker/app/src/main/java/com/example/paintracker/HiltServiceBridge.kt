package com.example.paintracker

import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltServiceBridge {
    fun getConfigService(): IConfigService
    fun getVisualiserLayerIoService(): IVisualiserLayerIoService
}
