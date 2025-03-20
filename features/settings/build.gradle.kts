import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.settings")
}

dependencies {
    implementation(projects.features.musicservice)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.androidx.appcompat)
    implementation(libs.oss.licenses)
}