import com.jooheon.toyplayer.configureCoroutineAndroid
import com.jooheon.toyplayer.configureKotest
import com.jooheon.toyplayer.configureKotlinAndroid
import com.jooheon.toyplayer.libs

plugins {
    id("com.android.library")
    id("toyplayer.verify.detekt")
}

configureKotlinAndroid()
configureKotest()
configureCoroutineAndroid()

dependencies {
    val libs = project.extensions.libs
    add("implementation", libs.findLibrary("jakewharton.timber").get())
}