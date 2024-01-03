plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.jooheon.toyplayer.features.common"

    defaultConfig {
        buildConfigField("String", "APPLICATION_ID", "\"com.jooheon.toyplayer\"")
        buildConfigField("String", "DEEPLINK_PREFIX", ("\"" + project.findProperty("DEEPLINK_SCHEME") + "://" + project.findProperty("DEEPLINK_BASE") + "\"") ?: "")
    }
}

dependencies {
    implementation(projects.domain)

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

    //hiltViewModel
    implementation(libs.hilt.androidx.navigation.compose)
}