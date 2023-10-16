import com.jooheon.toyproject.configureCoroutineAndroid
import com.jooheon.toyproject.configureHiltAndroid
import com.jooheon.toyproject.configureKotest
import com.jooheon.toyproject.configureKotlinAndroid

plugins {
    id("com.android.library")
    id("toyproject.verify.detekt")
}

configureKotlinAndroid()
configureKotest()
configureCoroutineAndroid()
