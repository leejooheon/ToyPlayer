import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
    id("toyplayer.android.hilt")
}

android {
    setNamespace("features.common")
}

dependencies {
    implementation(projects.domain.model)
    implementation(projects.domain.usecase)
    implementation(projects.core.resources)

    // glide
    implementation(libs.bumptech.glide)
    annotationProcessor(libs.bumptech.glide.compiler)
}