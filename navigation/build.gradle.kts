plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdk = Config.Android.compileSdkVersion
    buildToolsVersion = Config.Android.buildToolsVersion

    defaultConfig {
        minSdk = Config.Android.minSdkVersion
        targetSdk = Config.Android.targetSdkVersion
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Config.Libs.Android.Navigation.navigationUiKtx)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)

    implementation(Config.Libs.Android.ossLicenses)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
}
