plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jooheon.toyplayer.features.common"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.features.strings)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    // material icons
    implementation(libs.androidx.compose.material.iconsExtended)

    // theme
    implementation(libs.androidx.material)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // glide
    implementation(libs.bumptech.glide)
    kapt(libs.bumptech.glide.compiler)

    // media3
    implementation(libs.androidx.media3.session)

    //hiltViewModel
    implementation(libs.hilt.androidx.navigation.compose)
}