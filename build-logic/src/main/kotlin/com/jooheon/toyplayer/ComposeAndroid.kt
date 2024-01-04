package com.jooheon.toyplayer

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureComposeAndroid() {
    val libs = extensions.libs
    androidExtension.apply {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = libs.composeVersion
        }

        dependencies {
            val composeBom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(composeBom))
            add("androidTestImplementation", platform(composeBom))

            add("implementation", libs.findLibrary("androidx.compose.material3").get())
            add("implementation", libs.findLibrary("androidx.compose.ui").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.util").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview.build").get())

            // accompanist
            add("implementation",libs.findLibrary( "google.accompanist.permissions").get())
            add("implementation",libs.findLibrary( "google.accompanist.insets").get())
            add("implementation",libs.findLibrary( "google.accompanist.insets.ui").get())
            add("implementation",libs.findLibrary( "google.accompanist.systemuicontroller").get())
            add("implementation",libs.findLibrary( "google.accompanist.navigation.animation").get())
            add("implementation",libs.findLibrary( "google.accompanist.navigation.material").get())

            // 위치 바꾸자
            add("implementation", libs.findLibrary("jakewharton.timber").get())

            add("androidTestImplementation", libs.findLibrary("androidx.test.ext").get())
            add("androidTestImplementation", libs.findLibrary("androidx.test.espresso.core").get())
            add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())

            add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
            add("debugImplementation", libs.findLibrary("androidx.compose.ui.testManifest").get())
        }
    }
}