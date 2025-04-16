import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("data.music")
}

dependencies {
    implementation(projects.data.api)
    implementation(projects.domain.model)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.javax.inject)
    implementation(libs.androidx.media3.common)
    implementation(libs.jakewharton.timber)
}