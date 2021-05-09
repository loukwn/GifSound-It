import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
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
                    name = Config.BuildConfigFields.REDDIT_CLIENT_ID,
                    value = properties.getProperty(Config.BuildConfigFields.REDDIT_CLIENT_ID_PROP)
                            ?: "\"default_client_id_for_ci\""
            )
            buildConfigField(
                    type = Config.Types.STRING,
                    name = Config.BuildConfigFields.REDDIT_USER_AGENT,
                    value = properties.getProperty(Config.BuildConfigFields.REDDIT_USER_AGENT_PROP)
                            ?: "\"default_user_agent_for_ci\""
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true

            buildConfigField(
                    type = Config.Types.STRING,
                    name = Config.BuildConfigFields.REDDIT_CLIENT_ID,
                    value = properties.getProperty(Config.BuildConfigFields.REDDIT_CLIENT_ID_PROP)
                            ?: "\"default_client_id_for_ci\""
            )
            buildConfigField(
                    type = Config.Types.STRING,
                    name = Config.BuildConfigFields.REDDIT_USER_AGENT,
                    value = properties.getProperty(Config.BuildConfigFields.REDDIT_USER_AGENT_PROP)
                            ?: "\"default_user_agent_for_ci\""
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
    implementation(Config.Libs.Retrofit.loggingInterceptor)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)
    implementation(Config.Libs.Rx.rxKotlin)

    implementation(Config.Libs.timber)

    testImplementation(Config.TestLibs.Mockito.mockitoInline)
    testImplementation(Config.TestLibs.Mockito.mockitoKotlin)
    testImplementation(Config.TestLibs.jUnit)
}
