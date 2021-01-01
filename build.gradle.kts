buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath(Config.BuildPlugins.buildGradle)
        classpath(Config.BuildPlugins.kotlinGradlePlugin)
        classpath(Config.BuildPlugins.googleServices)
        classpath(Config.BuildPlugins.ktlintGradle)
        classpath(Config.BuildPlugins.firebaseCrashlyticsGradle)
        classpath(Config.BuildPlugins.hiltAndroidGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
