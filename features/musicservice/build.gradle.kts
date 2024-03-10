@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyplayer.android.library.hilt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.jooheon.toyplayer.features.musicservice"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.common)
//    testImplementation(projects.testing)

    // serialization
    implementation(libs.serialization.json)

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.hls)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.mediarouter)

    // coil
    implementation(libs.coil)

    // 위치 바꾸자
    implementation(libs.jakewharton.timber)
}