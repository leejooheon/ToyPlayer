plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
//    id("com.jooheon.kotlin-quality")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

@Suppress("UnstableApiUsage")
android {
    namespace = Releases.applicationId

    compileSdk = Versions.compileSdk
    defaultConfig {
        applicationId = Releases.applicationId

        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        versionCode = Releases.versionCode
        versionName = Releases.versionName

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
        sourceCompatibility = Versions.javaCompileVersion
        targetCompatibility = Versions.javaCompileVersion
    }
    packagingOptions {
        resources.excludes += "META-INF/AL2.0"
        resources.excludes += "META-INF/LGPL2.1"
    }
    dataBinding.enable = true
}

dependencies {
    implementation(project(path = ":data"))
    implementation(project(path = ":domain"))
    implementation(project(path = ":features:main"))
    implementation(project(path = ":features:musicservice"))
    implementation(project(path = ":features:musicplayer"))

    // util
    implementation(libs.androidx.multidex)


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

    // compose material3
    implementation("com.google.android.material:material:1.8.0-beta01")

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
    implementation(libs.androidx.mediarouter)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compile)
}