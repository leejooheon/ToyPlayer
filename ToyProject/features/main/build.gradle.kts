plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.jooheon.clean_architecture.features.main"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    viewBinding.enable = true
    dataBinding.enable = true

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":features:common"))
    implementation(project(path = ":features:splash"))
    implementation(project(path = ":features:github"))
    implementation(project(path = ":features:wikipedia"))
    implementation(project(path = ":features:map"))
    implementation(project(path = ":features:musicplayer"))
    implementation(project(path = ":features:musicservice"))
    implementation(project(path = ":features:setting"))
    implementation(project(path = ":features:splash"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    // android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.android.compose)
    kapt(libs.hilt.android.compiler)

    // hilt_work
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.worker)
    kapt(libs.hilt.worker.compiler)

    // Room
    implementation(libs.androidx.room)
    kapt(libs.androidx.room.compile)

    // accompanist
    implementation(libs.google.accompanist.permissions)
    implementation(libs.google.accompanist.insets)
    implementation(libs.google.accompanist.insets.ui)
    implementation(libs.google.accompanist.systemuicontroller)
    implementation(libs.google.accompanist.navigation.animation)
    implementation(libs.google.accompanist.navigation.material)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // glide
    implementation(libs.bumptech.glide)
    kapt(libs.bumptech.glide.compiler)

    // compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material.icon.extended)

    // compose material3
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowsizeclass)

    // compose preview
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // test
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.espresso.core)
}