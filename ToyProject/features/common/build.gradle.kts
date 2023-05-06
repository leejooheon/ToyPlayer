plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    namespace = "com.jooheon.clean_architecture.features.common"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
    implementation(project(path = ":domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // glide
    implementation(libs.bumptech.glide)
    annotationProcessor(libs.bumptech.glide.compiler)

    // timber
    implementation(libs.jakewharton.timber)

    // lottie
    implementation(libs.airbnb.android.lottie)

    // exoplayer
    implementation(libs.androidx.media3.exoplayer)

    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.espresso.core)
}