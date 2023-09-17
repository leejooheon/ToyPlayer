@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = App.nameSpace

    compileSdk = App.Versions.compileSdk
    defaultConfig {
        applicationId = App.applicationId

        minSdk = App.Versions.minSdk
        targetSdk = App.Versions.targetSdk

        versionCode = App.Releases.versionCode
        versionName = App.Releases.versionName

        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GITHUB_URL", "\"https://api.github.com\"")
        buildConfigField("String", "WIKIPEDIA_URL", "\"https://en.wikipedia.org\"")
        buildConfigField("String", "SUBWAY_URL", "\"http://swopenapi.seoul.go.kr/api/subway/\"")

        resValue("string", "google_maps_key", project.properties["GOOGLE_MAPS_API_KEY"] as String)
        resValue("string", "deeplink_prefix", project.properties["DEEPLINK_BASE"] as String)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
    compileOptions {
        sourceCompatibility = App.Versions.javaCompileVersion
        targetCompatibility = App.Versions.javaCompileVersion
    }
    kotlinOptions {
        jvmTarget = App.Versions.javaLanguageVersion
    }
    packagingOptions {
        resources.excludes += "META-INF/AL2.0"
        resources.excludes += "META-INF/LGPL2.1"
    }
    dataBinding.enable = true
}

dependencies {
    implementation(project(App.Module.data))
    implementation(project(App.Module.domain))
    implementation(project(App.Module.Features.main))
    implementation(project(App.Module.Features.musicService))
    implementation(project(App.Module.Features.musicPlayer))

    implementation(libs.jakewharton.timber)

    // androidx
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.material)

    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // hilt_work
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.worker)
    kapt(libs.hilt.worker.compiler)

    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // Network
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter)
    implementation(libs.squareup.retrofit.mock)
    implementation(libs.squareup.retrofit.interceptor)

    // Api debug
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    // mediaSession
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.mediarouter)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compile)
}