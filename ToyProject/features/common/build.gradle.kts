plugins {
    id("toyproject.android.library")
    id("toyproject.android.compose")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.jooheon.clean_architecture.toyproject.features.common"

    defaultConfig {
        buildConfigField("String", "APPLICATION_ID", "\"com.jooheon.clean_architecture.toyproject\"")
        buildConfigField("String", "DEEPLINK_PREFIX", ("\"" + project.findProperty("DEEPLINK_SCHEME") + "://" + project.findProperty("DEEPLINK_BASE") + "\"") ?: "")
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jakewharton.serialization.converter)

    implementation(libs.androidx.compose.material.iconsExtended)

    // coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // glide
    implementation(libs.bumptech.glide)
    kapt(libs.bumptech.glide.compiler)

    //hiltViewModel
    implementation(libs.hilt.androidx.navigation.compose)
}