@file:Suppress("DSL_SCOPE_VIOLATION")

import java.util.Properties


plugins {
    id("toyplayer.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
}

val localProperties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.jooheon.toyplayer"
    defaultConfig {
        applicationId = "com.jooheon.toyplayer"

        targetSdk = Integer.parseInt(libs.versions.android.sdk.compile.get())
        versionCode = Integer.parseInt(libs.versions.version.code.get())
        versionName = libs.versions.version.name.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties["keystore"].toString())
            storePassword = localProperties["keystore_pass"].toString()
            keyAlias = localProperties["key_alias"].toString()
            keyPassword = localProperties["key_pass"].toString()
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.features.main)
    implementation(projects.features.common)
    implementation(projects.features.musicservice)
    implementation(projects.data.repository)

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
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compile)

    // oss
    implementation(libs.oss.licenses)

    // LeakCanary
    debugImplementation(libs.squareup.leakcanary)
}