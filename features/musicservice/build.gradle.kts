@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyproject.android.library.hilt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject.features.musicservice"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.common)

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.mediarouter)

    // 위치 바꾸자
    implementation(libs.jakewharton.timber)
}