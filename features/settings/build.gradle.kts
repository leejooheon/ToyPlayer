import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.settings")
}

dependencies {
    implementation(projects.features.musicservice)
    implementation(projects.core.system)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.androidx.media3.common)

    implementation(libs.androidx.appcompat)
    implementation(libs.oss.licenses)

    implementation(libs.jakewharton.process.phoenix)
}