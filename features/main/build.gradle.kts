import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("features.main")
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.domain.usecase)

    implementation(projects.features.splash)
    implementation(projects.features.common)
    implementation(projects.features.musicservice)
    implementation(projects.features.settings)
    implementation(projects.features.player)
    implementation(projects.features.playlist)
    implementation(projects.features.library)
    implementation(projects.features.artist)
    implementation(projects.features.album)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.media3.session)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.kotlinx.immutable)
}