import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.splash")
}
dependencies {
    implementation(projects.features.musicservice)
    implementation(libs.androidx.media3.common)
}