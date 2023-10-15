plugins {
    id("toyproject.android.feature")
}

android {
    namespace = App.Module.Features.nameSpace + ".main"
}

dependencies {
    implementation(project(App.Module.domain))
    implementation(project(App.Module.Features.common))
    implementation(project(App.Module.Features.splash))
    implementation(project(App.Module.Features.github))
    implementation(project(App.Module.Features.wikipedia))
    implementation(project(App.Module.Features.map))
    implementation(project(App.Module.Features.musicPlayer))
    implementation(project(App.Module.Features.musicService))
    implementation(project(App.Module.Features.setting))
    implementation(project(App.Module.Features.splash))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.material.iconsExtended)
}