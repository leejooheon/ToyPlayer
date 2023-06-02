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
    namespace = "com.jooheon.clean_architecture.features.musicplayer"
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":features:common"))
    implementation(project(path = ":features:musicservice"))

    // android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // exoplayer
    implementation(libs.androidx.media3.exoplayer)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.android.compose)
    kapt(libs.hilt.android.compiler)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icon.extended)

    // compose motionlayout
    implementation(libs.androidx.constraintlayout.compose)

    // compose material3
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowsizeclass)

    // accompanist
    implementation(libs.google.accompanist.permissions)
    implementation(libs.google.accompanist.insets)
    implementation(libs.google.accompanist.insets.ui)
    implementation(libs.google.accompanist.systemuicontroller)
    implementation(libs.google.accompanist.navigation.animation)
    implementation(libs.google.accompanist.navigation.material)

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