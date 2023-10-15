plugins {
    id("toyproject.android.feature")
}

android {
    namespace = App.Module.Features.nameSpace + ".musicplayer"
}

dependencies {
    implementation(project(App.Module.Features.musicService))

    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.media3.exoplayer) // Media3
    implementation(libs.jaudiotagger) // AudioFileIO
}