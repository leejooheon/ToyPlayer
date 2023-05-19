plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

@Suppress("UnstableApiUsage")
android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "APPLICATION_ID", "\"com.jooheon.clean_architecture.toyproject\"")
        buildConfigField("String", "DEEPLINK_PREFIX", ("\"" + project.findProperty("DEEPLINK_SCHEME") + "://" + project.findProperty("DEEPLINK_BASE") + "\"") ?: "")
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
    namespace = "com.jooheon.clean_architecture.presentation"
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":features:common"))
    implementation(project(path = ":features:musicservice"))
    implementation(project(path = ":features:musicplayer"))

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

    // hilt_worker
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

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:29.0.3"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.1")

    // activity ui and system
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0-alpha03")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha01")
    implementation("com.google.android.material:material:1.8.0-beta01")

    // Constraint layout compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha05")

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

    // google map
    implementation("com.google.maps.android:maps-compose:2.5.3")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.mediarouter)

    // widget
    implementation("androidx.glance:glance-appwidget:1.0.0-alpha05")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // test
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.espresso.core)
}