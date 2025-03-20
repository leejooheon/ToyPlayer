@file:Suppress("DSL_SCOPE_VIOLATION")

import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")

    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("features.musicservice")
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.domain.usecase)

    implementation(projects.core.network)

    implementation(projects.features.common)
//    testImplementation(projects.testing)

    // serialization
    implementation(libs.kotlinx.serialization.json)

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.hls)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.mediarouter)

    // coil
    implementation(libs.coil)

    // timber
    implementation(libs.jakewharton.timber)

    // glide
    implementation(libs.bumptech.glide)
    annotationProcessor(libs.bumptech.glide.compiler)
}