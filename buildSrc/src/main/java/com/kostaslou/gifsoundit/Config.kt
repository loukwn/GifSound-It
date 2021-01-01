object Config {
    object Versions {
        const val kotlinVersion = "1.4.10"
        const val appcompatVersion = "1.2.0"
        const val cardviewVersion = "1.0.0"
        const val recyclerviewVersion = "1.2.0-beta01"
        const val annotationVersion = "1.1.0"
        const val lifecycleVersion = "2.2.0"
        const val navigationVersion = "2.3.1"
        const val constraintlayoutVersion = "2.0.4"
        const val materialVersion = "1.3.0-alpha03"
        const val swiperefreshlayoutVersion = "1.1.0"

        // testing
        const val espressoVersion = "3.3.0"
        const val core_testingVersion = "2.1.0"
        const val androidx_test_coreVersion = "1.3.0"
        const val test_runnerVersion = "1.3.0"
        const val test_rulesVersion = "1.3.0"
        const val junitVersion = "4.13.1"
        const val mockitoKotlinVersion = "2.2.0"
        const val mockitoInlineVersion = "3.6.0"
        const val ext_junitVersion = "1.1.2"

        // hilt
        const val hilt_viewmodelVersion = "1.0.0-alpha02"
        const val hiltVersion = "2.28-alpha"
        const val hilt_compilerVersion = "1.0.0-alpha02"

        // network
        const val retrofit2Version = "2.9.0"
        const val retrofit_logging_interceptorVersion = "4.9.0"
        const val moshiVersion = "2.0.0"

        // rx
        const val rxJava2Version = "2.2.20"
        const val rxAndroidVersion = "2.1.1"
        const val rxKotlinVersion = "2.4.0"

        // other
        const val glideVersion = "4.11.0"
        const val timberVersion = "4.7.1"
        const val firebase_coreVersion = "26.2.0"
    }

    object Types {
        const val STRING = "String"
    }

    object BuildPlugins {
        const val buildGradle = "com.android.tools.build:gradle:4.1.0"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
        const val googleServices = "com.google.gms:google-services:4.3.4"
        const val ktlintGradle = "org.jlleitschuh.gradle:ktlint-gradle:9.4.1"
        const val firebaseCrashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:2.3.0"
        const val hiltAndroidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:2.28-alpha"
    }

    object Android {
        const val buildToolsVersion = "29.0.3"
        const val minSdkVersion = 21
        const val targetSdkVersion = 30
        const val compileSdkVersion = 30
        const val applicationId = "com.kostaslou.gifsoundit"
        const val versionCode = 2
        const val versionName = "1.1.0"
        const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    object BuildConfigFields {
        const val REDDIT_CLIENT_ID = "RedditClientId"
        const val REDDIT_USER_AGENT = "RedditUserAgent"
        const val YOUTUBE_API_KEY = "YouTubeApiKey"
        const val REDDIT_CLIENT_ID_PROP = "GifSoundIt_RedditClientId"
        const val REDDIT_USER_AGENT_PROP = "GifSoundIt_RedditUserAgent"
        const val YOUTUBE_API_KEY_PROP = "GifSoundIt_YouTubeApiKey"
    }

    object Libs {
        const val kotlinStdLibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlinVersion}"
        const val timber = "com.jakewharton.timber:timber:${Versions.timberVersion}"
        const val glide = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
        const val youtubePlayer = "com.pierfrancescosoffritti.androidyoutubeplayer:core:10.0.3"

        object Android {
            const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompatVersion}"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayoutVersion}"
            const val material = "com.google.android.material:material:${Versions.materialVersion}"
            const val cardView = "androidx.cardview:cardview:${Versions.cardviewVersion}"
            const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerviewVersion}"
            const val annotation = "androidx.annotation:annotation:${Versions.annotationVersion}"
            const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}"
            const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swiperefreshlayoutVersion}"

            object Navigation {
                const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
                const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
            }
        }

        object Retrofit {
            const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit2Version}"
            const val adapterRxJava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit2Version}"
            const val converterMoshi = "com.squareup.retrofit2:converter-moshi:${Versions.moshiVersion}"
            const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.retrofit_logging_interceptorVersion}"
        }

        object Hilt {
            const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
            const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
            const val hiltLifecycleViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hilt_viewmodelVersion}"
            const val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hilt_compilerVersion}"
        }

        object Firebase {
            const val bom = "com.google.firebase:firebase-bom:${Versions.firebase_coreVersion}"
            const val analytics = "com.google.firebase:firebase-analytics:18.0.0"
            const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx:17.3.0"
        }

        object Rx {
            const val rxJava2 = "io.reactivex.rxjava2:rxjava:${Versions.rxJava2Version}"
            const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroidVersion}"
            const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlinVersion}"
        }

    }

    object TestLibs {
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
        const val kotlinTestJUnit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinVersion}"
        const val archCoreTesting = "androidx.arch.core:core-testing:${Versions.core_testingVersion}"
        const val jUnit = "junit:junit:${Versions.junitVersion}"

        object Mockito {
            const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInlineVersion}"
            const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlinVersion}"
        }
    }

    object AndroidTestLibs {
        const val core = "androidx.test:core:${Versions.androidx_test_coreVersion}"
        const val testRunner = "androidx.test:runner:${Versions.test_runnerVersion}"
        const val testRules = "androidx.test:rules:${Versions.test_rulesVersion}"
        const val extJunit = "androidx.test.ext:junit:${Versions.ext_junitVersion}"
        const val extJunitKtx = "androidx.test.ext:junit-ktx:${Versions.ext_junitVersion}"

        object Espresso {
            const val core = "androidx.test.espresso:espresso-core:${Versions.espressoVersion}"
            const val contrib = "androidx.test.espresso:espresso-contrib:${Versions.espressoVersion}"
        }
    }

    object Modules {
        const val common = ":common"
        const val featList = ":feat-list"
        const val featOpenGs = ":feat-opengs"
        const val featSettings = ":feat-settings"
        const val postData = ":postdata"
        const val navigation = ":navigation"
    }
}
