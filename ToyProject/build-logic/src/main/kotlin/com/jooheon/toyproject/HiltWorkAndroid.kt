package com.jooheon.toyproject

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureHiltWorkAndroid() {
    with(pluginManager) {
        apply("dagger.hilt.android.plugin")
        apply("org.jetbrains.kotlin.kapt")
    }

    val libs = extensions.libs
    dependencies {
        "implementation"(libs.findLibrary("androidx.work.runtime.ktx").get())
        "implementation"(libs.findLibrary("hilt.androidx.work").get())
        "kapt"(libs.findLibrary("hilt.androidx.work.compiler").get())
    }
}

internal class HiltWorkAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureHiltWorkAndroid()
        }
    }
}