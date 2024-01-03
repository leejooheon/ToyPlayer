import com.jooheon.toyplayer.configureKotlinAndroid
import com.jooheon.toyplayer.configureHiltAndroid
import com.jooheon.toyplayer.configureHiltWorkAndroid
import com.jooheon.toyplayer.configureKotestAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
configureHiltWorkAndroid()
configureKotestAndroid()
