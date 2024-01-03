plugins {
    id("toyplayer.android.feature")
}

android {
    namespace = "com.jooheon.toyplayer.features.musicplayer"
}

dependencies {
    implementation(projects.features.musicservice)

    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.media3.exoplayer) // Media3
    implementation(libs.jaudiotagger) // AudioFileIO
}