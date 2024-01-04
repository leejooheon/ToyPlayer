import com.jooheon.toyplayer.configureHiltAndroid
import com.jooheon.toyplayer.libs

plugins {
    id("toyplayer.android.library.hilt")
    id("toyplayer.android.compose")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

configureHiltAndroid()

dependencies {
    implementation(project(":domain"))
    implementation(project(":features:common"))
    testImplementation(project(":testing"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("hilt.androidx.navigation.compose").get())
    implementation(libs.findLibrary("androidx.compose.navigation").get())
    androidTestImplementation(libs.findLibrary("androidx.compose.navigation.test").get())

    implementation(libs.findLibrary("androidx.lifecycle.viewModel.compose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
}