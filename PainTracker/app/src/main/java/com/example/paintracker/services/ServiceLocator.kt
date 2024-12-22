package com.example.paintracker.services

import android.content.Context
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService

object ServiceLocator {
    lateinit var pathService: IPathService
    lateinit var configService: IConfigService
    lateinit var visualiserLayerIoService: IVisualiserLayerIoService

    fun initialize(context: Context) {
        pathService = PathService().apply { initialize(context) }
        configService = ConfigService().apply { initialize(context) }
        visualiserLayerIoService = VisualiserLayerIoService()
    }
}