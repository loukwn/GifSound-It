plugins {
    id("com.android.library")
    kotlin("android")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("kapt")
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Config.Android.composeCompilerVersion
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.material)

    implementation(Config.Libs.Android.Compose.ui)
    implementation(Config.Libs.Android.Compose.material)
    implementation(Config.Libs.Android.Compose.foundation)
    implementation(Config.Libs.Android.Compose.uiTooling)
    implementation(Config.Libs.Android.Compose.compiler)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
}
