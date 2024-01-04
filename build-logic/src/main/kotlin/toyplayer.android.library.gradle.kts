import com.jooheon.toyplayer.configureCoroutineAndroid
import com.jooheon.toyplayer.configureKotest
import com.jooheon.toyplayer.configureKotlinAndroid

plugins {
    id("com.android.library")
    id("toyplayer.verify.detekt")
}

configureKotlinAndroid()
configureKotest()
configureCoroutineAndroid()
