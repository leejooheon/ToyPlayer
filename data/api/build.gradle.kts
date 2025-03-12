import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("data.api")
}

dependencies {
    implementation(projects.domain.model)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.jakewharton.timber)

    // Converter
    implementation(libs.jakewharton.serialization.converter)
    implementation(libs.squareup.retrofit.scalars.converter)

    // Network
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter)
    implementation(libs.squareup.retrofit.mock)
    implementation(libs.squareup.retrofit.interceptor)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)
}