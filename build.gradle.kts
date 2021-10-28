buildscript {
    repositories {
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath(Config.BuildPlugins.buildGradle)
        classpath(Config.BuildPlugins.kotlinGradlePlugin)
        classpath(Config.BuildPlugins.googleServices)
        classpath(Config.BuildPlugins.ktlintGradle)
        classpath(Config.BuildPlugins.firebaseCrashlyticsGradle)
        classpath(Config.BuildPlugins.hiltAndroidGradlePlugin)
        classpath(Config.BuildPlugins.ossVersionsPlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    // This is for a bug with a certain a version of mockk and one of its dependencies
    // https://github.com/mockk/mockk/issues/281
    configurations.all {
        resolutionStrategy {
            force("org.objenesis:objenesis:2.6")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
