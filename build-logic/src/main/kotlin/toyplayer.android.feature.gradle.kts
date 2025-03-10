import com.jooheon.toyplayer.configureHiltAndroid
import com.jooheon.toyplayer.libs

plugins {
    id("toyplayer.android.library")
    id("toyplayer.android.compose")
    id("toyplayer.android.hilt")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

configureHiltAndroid()

dependencies {
    implementation(project(":domain:model"))
    implementation(project(":domain:usecase"))

    implementation(project(":core:resources"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))

    implementation(project(":features:common"))
//    testImplementation(project(":testing"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("hilt.androidx.navigation.compose").get())
    implementation(libs.findLibrary("androidx.compose.navigation").get())
    androidTestImplementation(libs.findLibrary("androidx.compose.navigation.test").get())

    implementation(libs.findLibrary("androidx.lifecycle.viewModel.compose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
}