import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
}

android {
    setNamespace("features.commonui")
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.resources)
    implementation(projects.domain.model)
    implementation(projects.features.common)

    // glide
    implementation(libs.bumptech.glide)
    annotationProcessor(libs.bumptech.glide.compiler)
    implementation(libs.bumptech.glide.compose)

    // audioFileIO
    implementation(libs.jaudiotagger)

    // palette
    implementation(libs.androidx.palette)
}