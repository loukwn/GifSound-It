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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.common))
    implementation(project(Config.Modules.navigation))

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    implementation(Config.Libs.timber)

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)
    implementation(Config.Libs.Android.Navigation.navigationUiKtx)
    implementation(Config.Libs.Android.constraintLayout)
    implementation(Config.Libs.Android.material)

    testImplementation(Config.TestLibs.jUnit)
    testImplementation(Config.TestLibs.mockk)
}
