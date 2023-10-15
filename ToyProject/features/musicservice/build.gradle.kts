@file:Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("toyproject.android.library.hilt")
    id("kotlin-parcelize")
}

android {
    namespace = App.Module.Features.nameSpace + ".musicservice"
}

dependencies {
    implementation(project(App.Module.domain))
    implementation(project(App.Module.Features.common))

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.mediarouter)

    // 위치 바꾸자
    implementation(libs.jakewharton.timber)
}