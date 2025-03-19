import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("features.common")
}

dependencies {
    implementation(projects.domain.model)

    implementation(projects.core.resources)
    implementation(projects.core.navigation)
    implementation(projects.core.designsystem)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    // material icons
    implementation(libs.androidx.compose.material.iconsExtended)

    // theme
    implementation(libs.androidx.material)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.bumptech.glide.compose)

    // glide
    implementation(libs.bumptech.glide)
    annotationProcessor(libs.bumptech.glide.compiler)

    // media3
    implementation(libs.androidx.media3.session)

    // hiltViewModel
    implementation(libs.hilt.androidx.navigation.compose)

    // audioFileIO
    implementation(libs.jaudiotagger)

    implementation("androidx.palette:palette:1.0.0")
}