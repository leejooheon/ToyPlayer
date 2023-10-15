plugins {
    id("toyproject.android.feature")
}

android {
    namespace = App.Module.Features.nameSpace + ".setting"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)
}