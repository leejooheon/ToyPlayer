@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyplayer.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jooheon.toyplayer.data"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.strings)

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    // media3
    implementation(libs.androidx.media3.common)

    // Network
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter)
    implementation(libs.squareup.retrofit.mock)
    implementation(libs.squareup.retrofit.interceptor)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compile)
}