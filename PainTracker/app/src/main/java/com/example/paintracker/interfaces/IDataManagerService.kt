package com.example.paintracker.interfaces

import com.example.paintracker.data.PainEntry

interface IDataManagerService {
    fun ListAllPainEntries(): List<PainEntry>
}