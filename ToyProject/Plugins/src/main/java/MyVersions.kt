import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

object MyVersions {
    const val versionCode = 10000
    const val versionName = "1.0.0"

    const val compileSdk = 34
    const val targetSdk = 34
    const val minSdk = 26

    val javaCompileVersion = JavaVersion.VERSION_11
    val javaLanguageVersion: JavaLanguageVersion = JavaLanguageVersion.of(11)
}