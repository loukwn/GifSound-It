import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    // Reads the local properties for the App secrets and if it cannot find them, it will get them from
    // the empty ones
    val properties = Properties()
    val localProperties: File = rootProject.file("local.properties")
    if (localProperties.exists()) {
        localProperties.inputStream().use { properties.load(it) }
    }

    compileSdkVersion(Config.Android.compileSdkVersion)
    buildToolsVersion(Config.Android.buildToolsVersion)

    defaultConfig {
        minSdkVersion(Config.Android.minSdkVersion)
        targetSdkVersion(Config.Android.targetSdkVersion)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                Config.Types.STRING,
                Config.BuildConfigFields.YOUTUBE_API_KEY,
                properties.getProperty(Config.BuildConfigFields.YOUTUBE_API_KEY_PROP)
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true

            buildConfigField(
                Config.Types.STRING,
                Config.BuildConfigFields.YOUTUBE_API_KEY,
                properties.getProperty(Config.BuildConfigFields.YOUTUBE_API_KEY_PROP)
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.common))
    implementation(project(Config.Modules.navigation))

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.constraintLayout)

    implementation(Config.Libs.glide)

    implementation(Config.Libs.timber)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    implementation(Config.Libs.Hilt.hiltLifecycleViewModel)
    kapt(Config.Libs.Hilt.hiltCompiler)

    api(Config.Libs.youtubePlayer)

    testImplementation(Config.TestLibs.archCoreTesting)
    testImplementation(Config.TestLibs.Mockito.mockitoInline)
    testImplementation(Config.TestLibs.Mockito.mockitoKotlin)
    testImplementation(Config.TestLibs.jUnit)
}
