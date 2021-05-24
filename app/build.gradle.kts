import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    // Reads the local properties for the App secrets and if it cannot find them, it will get them from
    // the empty ones
    val properties = Properties()
    var localProperties: File = rootProject.file("local.properties")
    if (localProperties.exists()) {
        localProperties.inputStream().use { properties.load(it) }
    } else {
        localProperties = rootProject.file("empty.properties")
        localProperties.inputStream().use { properties.load(it) }
    }

    signingConfigs {
        create("release") {
            storeFile = file(properties.getProperty("storeFilePath"))
            storePassword = properties.getProperty("storePassword")
            keyPassword = properties.getProperty("keyPassword")
            keyAlias = properties.getProperty("keyAlias")
        }
    }

    compileSdkVersion(Config.Android.compileSdkVersion)

    defaultConfig {
        applicationId = Config.Android.applicationId
        minSdkVersion(Config.Android.minSdkVersion)
        targetSdkVersion(Config.Android.targetSdkVersion)
        versionCode = Config.Android.versionCode
        versionName = Config.Android.versionName
        testInstrumentationRunner = Config.Android.testInstrumentationRunner
        signingConfig = signingConfigs.getByName("release")
    }
    buildTypes {
        getByName("release") {
            resValue("string", "app_version", "v${Config.Android.versionName}")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Gifsound It - Dev")
            resValue("string", "app_version", "v${Config.Android.versionName}")
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

repositories {
    maven { url = uri("https://plugins.gradle.org/m2/") }
    google()
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.featList))
    implementation(project(Config.Modules.featOpenGs))
    implementation(project(Config.Modules.featSettings))
    implementation(project(Config.Modules.common))
    implementation(project(Config.Modules.postData))
    implementation(project(Config.Modules.navigation))

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)
    implementation(Config.Libs.Android.Navigation.navigationUiKtx)
    implementation(Config.Libs.Android.ossLicenses)

    implementation(Config.Libs.Retrofit.retrofit)
    implementation(Config.Libs.Retrofit.adapterRxJava2)
    implementation(Config.Libs.Retrofit.converterMoshi)
    implementation(Config.Libs.Retrofit.loggingInterceptor)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.timber)

    debugImplementation(Config.Libs.leakCanary)

    implementation(Config.Libs.Firebase.bom)
    implementation(Config.Libs.Firebase.analytics)
    implementation(Config.Libs.Firebase.crashlyticsKtx)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    androidTestImplementation(Config.AndroidTestLibs.core)
    androidTestImplementation(Config.AndroidTestLibs.testRunner)
    androidTestImplementation(Config.AndroidTestLibs.testRules)
    androidTestImplementation(Config.AndroidTestLibs.extJunit)
    androidTestImplementation(Config.AndroidTestLibs.extJunitKtx)
    androidTestImplementation(Config.AndroidTestLibs.Espresso.core)
    androidTestImplementation(Config.AndroidTestLibs.Espresso.contrib)

    testImplementation(Config.TestLibs.mockk)
}
