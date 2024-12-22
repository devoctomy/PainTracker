package com.example.paintracker

import android.content.Context
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.services.ConfigService
import com.example.paintracker.services.PathService
import com.example.paintracker.services.VisualiserLayerIoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun providePathService(@ApplicationContext context: Context): IPathService {
        val pathService = PathService()
        pathService.initialize(context)
        return pathService
    }

    @Provides
    @Singleton
    fun provideConfigService(@ApplicationContext context: Context): IConfigService {
        val configService = ConfigService()
        configService.initialize(context)
        return configService
    }

    @Provides
    @Singleton
    fun provideVisualiserLayerIoService(pathService: IPathService): IVisualiserLayerIoService {
        return VisualiserLayerIoService(pathService)
    }
}