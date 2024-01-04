package com.jooheon.toyplayer

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureVerifyDetekt() {
    with(pluginManager) {
        apply("io.gitlab.arturbosch.detekt")
    }

    val libs = extensions.libs
    dependencies {
        "detektPlugins"(libs.findLibrary("verify.detekt.formatting").get())
    }
}