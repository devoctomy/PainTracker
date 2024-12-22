// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false // Hilt plugin for modules that need it
    alias(libs.plugins.ksp) apply false // KSP plugin for annotation processing
}