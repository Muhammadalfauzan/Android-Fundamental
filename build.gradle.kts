

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    dependencies {

        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.3")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.11" apply false
}