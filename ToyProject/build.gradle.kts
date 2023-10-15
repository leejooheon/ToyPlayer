@file:Suppress("DSL_SCOPE_VIOLATION")
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io" )
    }
    dependencies {
        classpath(libs.google.gms.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
}

apply {
    from("gradle/dependencyGraph.gradle")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io" )
    }
}