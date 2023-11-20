plugins {
    id("toyproject.android.feature")
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject.features.main"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.common)
    implementation(projects.features.splash)
    implementation(projects.features.musicplayer)
    implementation(projects.features.musicservice)
    implementation(projects.features.setting)
    implementation(projects.features.splash)

    implementation(libs.androidx.media3.session)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)
}