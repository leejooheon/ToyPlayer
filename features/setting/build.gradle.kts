import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.setting")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)
}