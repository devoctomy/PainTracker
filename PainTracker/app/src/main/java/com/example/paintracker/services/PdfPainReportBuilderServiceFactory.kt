package com.example.paintracker.services

import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.INotesIoService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IPdfPainReportBuilderService
import com.example.paintracker.interfaces.IPdfPainReportBuilderServiceFactory
import javax.inject.Inject

class PdfPainReportBuilderServiceFactory @Inject constructor(
    private val configService: IConfigService,
    private val pathService: IPathService,
    private val dataManagerService: IDataManagerService,
    private val notesIoService: INotesIoService
) : IPdfPainReportBuilderServiceFactory {

    override fun create(): IPdfPainReportBuilderService {
        return PdfPainReportBuilderService(configService, pathService, dataManagerService, notesIoService)
    }

}