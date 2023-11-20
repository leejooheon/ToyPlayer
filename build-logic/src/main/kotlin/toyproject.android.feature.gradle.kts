import com.jooheon.toyproject.configureHiltAndroid
import com.jooheon.toyproject.libs

plugins {
    id("toyproject.android.library.hilt")
    id("toyproject.android.compose")
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

    val libs = project.extensions.libs
    implementation(libs.findLibrary("hilt.androidx.navigation.compose").get())
    implementation(libs.findLibrary("androidx.compose.navigation").get())
    androidTestImplementation(libs.findLibrary("androidx.compose.navigation.test").get())

    implementation(libs.findLibrary("androidx.lifecycle.viewModel.compose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
}
