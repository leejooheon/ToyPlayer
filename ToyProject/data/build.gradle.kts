@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
}

android {
    namespace = App.nameSpace + ".data"

    compileSdk = App.Versions.compileSdk

    defaultConfig {
        minSdk = App.Versions.minSdk
        targetSdk = App.Versions.targetSdk
    }
    compileOptions {
        sourceCompatibility = App.Versions.javaCompileVersion
        targetCompatibility = App.Versions.javaCompileVersion
    }
    kotlinOptions {
        jvmTarget = App.Versions.javaLanguageVersion
    }
    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(App.Module.domain))

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // Network
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter)
    implementation(libs.squareup.retrofit.mock)
    implementation(libs.squareup.retrofit.interceptor)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compile)
}