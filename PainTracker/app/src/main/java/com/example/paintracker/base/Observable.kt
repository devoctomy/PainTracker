package com.example.paintracker.base

open class Observable {
    private val listeners = mutableListOf<(String, Any?, Any?) -> Unit>()

    fun addChangeListener(listener: (propertyName: String, oldValue: Any?, newValue: Any?) -> Unit) {
        listeners.add(listener)
    }

    fun removeChangeListener(listener: (propertyName: String, oldValue: Any?, newValue: Any?) -> Unit) {
        listeners.remove(listener)
    }

    protected fun notifyListeners(propertyName: String, oldValue: Any?, newValue: Any?) {
        listeners.forEach { it(propertyName, oldValue, newValue) }
    }
}