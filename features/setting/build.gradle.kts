plugins {
    id("toyplayer.android.feature")
}

android {
    namespace = "com.jooheon.toyplayer.features.setting"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)
}