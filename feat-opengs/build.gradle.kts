import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
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
                    type = Config.Types.STRING,
                    name = Config.BuildConfigFields.YOUTUBE_API_KEY,
                    value = properties.getProperty(Config.BuildConfigFields.YOUTUBE_API_KEY_PROP)
                            ?: "\"default_youtube_api_key_for_ci\""
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true

            buildConfigField(
                    type = Config.Types.STRING,
                    name = Config.BuildConfigFields.YOUTUBE_API_KEY,
                    value = properties.getProperty(Config.BuildConfigFields.YOUTUBE_API_KEY_PROP)
                            ?: "\"default_youtube_api_key_for_ci\""
            )
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

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.material)
    implementation(Config.Libs.Android.coreKtx)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)
    implementation(Config.Libs.Android.constraintLayout)

    implementation(Config.Libs.glide)

    implementation(Config.Libs.timber)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    implementation(Config.Libs.Hilt.hiltLifecycleViewModel)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    api(Config.Libs.youtubePlayer)

    testImplementation(Config.TestLibs.mockk)
    testImplementation(Config.TestLibs.jUnit)
}
