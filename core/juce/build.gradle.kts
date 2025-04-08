import com.jooheon.toyplayer.setNamespace

plugins {
    id("toyplayer.android.library")
}

android {
    setNamespace("core.juce")

    externalNativeBuild {
        cmake {
            path("CMakeLists.txt")
        }
    }

    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17")
            }
        }
//        ndk {
//            //noinspection ChromeOsAbiSupport
//            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
//        }
    }
}

dependencies {

}