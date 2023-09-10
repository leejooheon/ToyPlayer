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
    namespace = App.Module.Features.nameSpace + ".map"
    compileSdk = App.Versions.compileSdk

    defaultConfig {
        minSdk = App.Versions.minSdk
        targetSdk = App.Versions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
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

    // android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.android.compose)
    kapt(libs.hilt.android.compiler)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // compose BOM
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // compose
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.materialWindow)
    implementation(libs.androidx.constraintlayout.compose)

    // compose preview
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // accompanist
    implementation(libs.google.accompanist.permissions)

    // google map
    implementation("com.google.maps.android:maps-compose:2.5.3")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // test
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.espresso.core)
}