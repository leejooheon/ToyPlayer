@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyproject.android.application")
//    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject"
    defaultConfig {
        applicationId = "com.jooheon.clean_architecture.toyproject"
        versionCode = Integer.parseInt(libs.versions.version.code.get())
        versionName = libs.versions.version.name.get()

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
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.features.main)
    implementation(projects.features.musicservice)
    implementation(projects.features.musicplayer)

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
//    ksp(libs.androidx.room.compile)
    kapt(libs.androidx.room.compile)

    // LearkCanary
    debugImplementation(libs.squareup.leakcanary)
}