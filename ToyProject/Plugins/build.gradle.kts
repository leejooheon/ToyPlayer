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
            id = "toyproject.android.hilt"
            implementationClass = "com.jooheon.toyproject.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "toyproject.kotlin.hilt"
            implementationClass = "com.jooheon.toyproject.HiltKotlinPlugin"
        }
    }
}
