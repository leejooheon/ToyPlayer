plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.verify.detekt.plugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "toyplayer.android.hilt"
            implementationClass = "com.jooheon.toyplayer.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "toyplayer.kotlin.hilt"
            implementationClass = "com.jooheon.toyplayer.HiltKotlinPlugin"
        }
    }
}
