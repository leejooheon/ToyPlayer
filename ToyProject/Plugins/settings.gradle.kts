// 참고자료: https://proandroiddev.com/using-version-catalog-on-android-projects-82d88d2f79e5
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
