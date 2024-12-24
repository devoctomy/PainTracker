package com.example.paintracker.data

import com.example.paintracker.base.Observable
import com.example.paintracker.interfaces.IPainContext
import java.time.LocalDate
import kotlin.properties.Delegates

class PainContext : Observable(), IPainContext {
    override var selectedDate: LocalDate by Delegates.observable(LocalDate.now()) { _, oldValue, newValue ->
        notifyListeners("selectedDate", oldValue, newValue)
    }
}