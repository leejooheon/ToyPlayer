import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.musicplayer")
}

dependencies {
    implementation(projects.features.musicservice)

    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.constraintlayout.compose)

    // media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)

    implementation(libs.jaudiotagger) // AudioFileIO
}