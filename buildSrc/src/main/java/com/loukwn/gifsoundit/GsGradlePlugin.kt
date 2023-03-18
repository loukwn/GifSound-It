package com.loukwn.gifsoundit

import com.android.build.api.dsl.ApplicationBaseFlavor
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryBaseFlavor
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.VariantDimension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.util.Properties

class GsApplicationGradlePlugin : GsGradlePlugin(GsGradlePluginType.Application)
class GsPresentationComposeGradlePlugin :
    GsGradlePlugin(GsGradlePluginType.Presentation(inCompose = true, nonScreen = false))

class GsPresentationNonScreenComposeGradlePlugin :
    GsGradlePlugin(GsGradlePluginType.Presentation(inCompose = true, nonScreen = true))

class GsPresentationViewGradlePlugin :
    GsGradlePlugin(GsGradlePluginType.Presentation(inCompose = false, nonScreen = false))

class GsLibraryGradlePlugin : GsGradlePlugin(GsGradlePluginType.Library)

abstract class GsGradlePlugin(private val type: GsGradlePluginType) : Plugin<Project> {
    override fun apply(project: Project) {
        project.setupPluginsSection(type)
        project.setupAndroidSection(type)
    }

    private fun Project.setupPluginsSection(type: GsGradlePluginType) {
        plugins.apply(getAndroidPlugin(type))
        plugins.apply("org.jetbrains.kotlin.android")
        plugins.apply("kotlin-kapt")
        extensions.findByType(KaptExtension::class.java)?.let { kapt ->
            kapt.correctErrorTypes = true
        }

        if (type.usesHilt) {
            plugins.apply("dagger.hilt.android.plugin")
        }

        if (type is GsGradlePluginType.Application) {
            plugins.apply("com.google.android.gms.oss-licenses-plugin")
            plugins.apply("com.google.gms.google-services")
            plugins.apply("com.google.firebase.crashlytics")
        }
    }

    private fun Project.setupAndroidSection(type: GsGradlePluginType) {
        val android = extensions.getByType(CommonExtension::class.java)
        val androidComponents = extensions.getByType(AndroidComponentsExtension::class.java)

        android.apply {
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

            if (type is GsGradlePluginType.Application) {
                signingConfigs {
                    create("release") {
                        storeFile = file(properties.getProperty("storeFilePath"))
                        storePassword = properties.getProperty("storePassword")
                        keyPassword = properties.getProperty("keyPassword")
                        keyAlias = properties.getProperty("keyAlias")
                    }
                }
            }

            compileSdk = Config.Android.compileSdkVersion
            if (type.isAndroidLibrary) {
                buildToolsVersion = Config.Android.buildToolsVersion
            }
            defaultConfig {
                minSdk = Config.Android.minSdkVersion
                if (this is LibraryBaseFlavor) {
                    targetSdk = Config.Android.targetSdkVersion
                } else if (this is ApplicationBaseFlavor) {
                    targetSdk = Config.Android.targetSdkVersion
                    if (type is GsGradlePluginType.Application) {
                        applicationId = Config.Android.applicationId
                        versionCode = Config.Android.versionCode
                        versionName = Config.Android.versionName
                        testInstrumentationRunner = Config.Android.testInstrumentationRunner
                        signingConfig = signingConfigs.getByName("release")
                    }
                }
            }

            buildTypes {
                getByName("release") {
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                    isMinifyEnabled = true
                    if (type is GsGradlePluginType.Application) {
                        isShrinkResources = true
                    }
                    addBuildConfigFields(properties)
                }
                getByName("debug") {
                    isMinifyEnabled = false
                    if (type is GsGradlePluginType.Application) {
                        isShrinkResources = false
                        if (this is ApplicationBuildType) {
                            applicationIdSuffix = ".debug"
                            resValue("string", "app_name", "Gifsound It - Dev")
                        }
                    }
                    addBuildConfigFields(properties)
                }
            }

            packagingOptions {
                resources {
                    excludes.add("META-INF/LICENSE**")
                }
            }
        }

        androidComponents.finalizeDsl { androidExtension ->
            androidExtension.apply {
                if (type is GsGradlePluginType.Presentation) {
                    if (type.inCompose) {
                        buildFeatures.apply {
                            compose = true
                        }

                        composeOptions.apply {
                            kotlinCompilerExtensionVersion = Config.Android.composeCompilerVersion
                        }
                    } else {
                        buildFeatures.apply {
                            viewBinding = true
                        }
                    }
                }

                compileOptions.apply {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }

                tasks.withType<KotlinCompile>().configureEach {
                    kotlinOptions.jvmTarget = "1.8"
                    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
                }
            }
        }
    }

    private fun VariantDimension.addBuildConfigFields(properties: Properties) {
        buildConfigField(
            type = Config.Types.STRING,
            name = Config.BuildConfigFields.YOUTUBE_API_KEY,
            value = properties.getProperty(Config.BuildConfigFields.YOUTUBE_API_KEY_PROP)
                ?: "\"default_youtube_api_key_for_ci\""
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

    private fun getAndroidPlugin(type: GsGradlePluginType): String =
        if (type.isAndroidLibrary) "com.android.library" else "com.android.application"
}

private val GsGradlePluginType.isAndroidLibrary: Boolean
    get() = when (this) {
        GsGradlePluginType.Library,
        is GsGradlePluginType.Presentation -> true

        GsGradlePluginType.Application -> false
    }

private val GsGradlePluginType.usesHilt: Boolean
    get() = when (this) {
        is GsGradlePluginType.Presentation -> !this.nonScreen
        GsGradlePluginType.Application -> true

        GsGradlePluginType.Library -> false
    }


sealed class GsGradlePluginType {
    object Application : GsGradlePluginType()
    data class Presentation(val inCompose: Boolean, val nonScreen: Boolean) : GsGradlePluginType()
    object Library : GsGradlePluginType()
}
