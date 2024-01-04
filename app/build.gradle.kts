@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyplayer.android.application")
//    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jooheon.toyplayer"
    defaultConfig {
        applicationId = "com.jooheon.toyplayer"
        versionCode = Integer.parseInt(libs.versions.version.code.get())
        versionName = libs.versions.version.name.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.features.main)
    implementation(projects.features.common)
    implementation(projects.features.musicservice)
    implementation(projects.features.musicplayer)

    // theme
    implementation(libs.androidx.material)

    // media3
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.session)

    // log
    implementation(libs.jakewharton.timber)

    // Network
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter)
    implementation(libs.squareup.retrofit.mock)
    implementation(libs.squareup.retrofit.interceptor)

    // Api debug
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
//    ksp(libs.androidx.room.compile)
    kapt(libs.androidx.room.compile)

    // LearkCanary
    debugImplementation(libs.squareup.leakcanary)
}