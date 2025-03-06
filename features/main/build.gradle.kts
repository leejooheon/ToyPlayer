import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
}

android {
    setNamespace("features.main")
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.domain.usecase)

    implementation(projects.features.common)
    implementation(projects.features.splash)
    implementation(projects.features.musicplayer)
    implementation(projects.features.musicservice)
    implementation(projects.features.setting)
    implementation(projects.features.splash)

    implementation(libs.androidx.media3.session)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.kotlinx.immutable)
}