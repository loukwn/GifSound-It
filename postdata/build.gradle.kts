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
                Config.BuildConfigFields.REDDIT_CLIENT_ID,
                properties.getProperty(Config.BuildConfigFields.REDDIT_CLIENT_ID_PROP)
            )
            buildConfigField(
                Config.Types.STRING,
                Config.BuildConfigFields.REDDIT_USER_AGENT,
                properties.getProperty(Config.BuildConfigFields.REDDIT_USER_AGENT_PROP)
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true

            buildConfigField(
                Config.Types.STRING,
                Config.BuildConfigFields.REDDIT_CLIENT_ID,
                properties.getProperty(Config.BuildConfigFields.REDDIT_CLIENT_ID_PROP)
            )
            buildConfigField(
                Config.Types.STRING,
                Config.BuildConfigFields.REDDIT_USER_AGENT,
                properties.getProperty(Config.BuildConfigFields.REDDIT_USER_AGENT_PROP)
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.common))

    implementation(Config.Libs.Retrofit.retrofit)
    implementation(Config.Libs.Retrofit.adapterRxJava2)
    implementation(Config.Libs.Retrofit.converterMoshi)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    implementation(Config.Libs.Hilt.hiltLifecycleViewModel)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)
    implementation(Config.Libs.Rx.rxKotlin)

    implementation(Config.Libs.timber)

    testImplementation(Config.TestLibs.archCoreTesting)
    testImplementation(Config.TestLibs.Mockito.mockitoInline)
    testImplementation(Config.TestLibs.Mockito.mockitoKotlin)
    testImplementation(Config.TestLibs.jUnit)
}