import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

object App {
    const val applicationId = "com.jooheon.clean_architecture.toyproject"
    const val nameSpace = "com.jooheon.clean_architecture.toyproject"
    object Releases {
        const val versionCode = 10000
        const val versionName = "1.0.0"
    }

    object Module {
        const val app = ":app"
        const val data = ":data"
        const val domain = ":domain"

        object Features {
            const val nameSpace = App.nameSpace + ".features"

            const val musicService = ":features:musicservice"

            const val common = ":features:common"
            const val main = ":features:main"
            const val splash = ":features:splash"
            const val setting = ":features:setting"

            const val musicPlayer = ":features:musicplayer"
            const val github = ":features:github"
            const val map = ":features:map"
            const val wikipedia = ":features:wikipedia"
        }
    }
}