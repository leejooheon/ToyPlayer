@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = App.Module.Features.nameSpace + ".musicservice"
    compileSdk = App.Versions.compileSdk

    defaultConfig {
        minSdk = App.Versions.minSdk
        targetSdk = App.Versions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles("proguard-android.txt", "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = App.Versions.javaCompileVersion
        targetCompatibility = App.Versions.javaCompileVersion
    }
    kotlinOptions {
        jvmTarget = App.Versions.javaLanguageVersion
    }
}

dependencies {
    implementation(project(App.Module.domain))
    implementation(project(App.Module.Features.common))

    implementation(libs.androidx.core.ktx)

    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.mediarouter)

    // timber
    implementation(libs.jakewharton.timber)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.espresso.core)
}