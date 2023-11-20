package com.jooheon.toyproject

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCoroutineAndroid() {
    val libs = extensions.libs
    configureCoroutineKotlin()
    dependencies {
        "implementation"(libs.findLibrary("kotlinx.coroutines.android").get())
    }
}

internal fun Project.configureCoroutineKotlin() {
    val libs = extensions.libs
    dependencies {
        "implementation"(libs.findLibrary("kotlinx.coroutines.core").get())
        "implementation"(libs.findLibrary("kotlinx.coroutines.guava").get())
        "testImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
    }
}
