package com.jooheon.toyplayer

import org.gradle.api.Project

fun Project.setNamespace(name: String) {
    androidExtension.apply {
        namespace = "com.jooheon.toyplayer.$name"
    }
}