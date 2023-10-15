@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyproject.android.application")
//    alias(libs.plugins.ksp)
}

android {
    namespace = App.nameSpace
    defaultConfig {
        applicationId = App.applicationId
        versionCode = App.Releases.versionCode
        versionName = App.Releases.versionName

//        multiDexEnabled = true
//        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "GITHUB_URL", "\"https://api.github.com\"")
        buildConfigField("String", "WIKIPEDIA_URL", "\"https://en.wikipedia.org\"")
        buildConfigField("String", "SUBWAY_URL", "\"http://swopenapi.seoul.go.kr/api/subway/\"")

        resValue("string", "google_maps_key", project.properties["GOOGLE_MAPS_API_KEY"] as String)
        resValue("string", "deeplink_prefix", project.properties["DEEPLINK_BASE"] as String)
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
    implementation(project(App.Module.data))
    implementation(project(App.Module.domain))
    implementation(project(App.Module.Features.main))
    implementation(project(App.Module.Features.musicService))
    implementation(project(App.Module.Features.musicPlayer))

    // theme
    implementation(libs.androidx.material)

    // media3
    implementation(libs.androidx.media3.common)

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
    kapt(libs.androidx.room.compile)

    // LearkCanary
    debugImplementation(libs.squareup.leakcanary)
}