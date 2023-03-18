@file:Suppress("PackageDirectoryMismatch")

object Config {

    object Types {
        const val STRING = "String"
    }

    object BuildPlugins {
        const val buildGradle = "com.android.tools.build:gradle:_"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:_"
        const val googleServices = "com.google.gms:google-services:_"
        const val ktlintGradle = "org.jlleitschuh.gradle:ktlint-gradle:_"
        const val firebaseCrashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:_"
        const val hiltAndroidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:_"
        const val ossVersionsPlugin = "com.google.android.gms:oss-licenses-plugin:_"
    }

    object Android {
        const val buildToolsVersion = "30.0.3"
        const val minSdkVersion = 21
        const val targetSdkVersion = 33
        const val compileSdkVersion = 33
        const val applicationId = "com.loukwn.gifsoundit"
        const val versionCode = 4
        const val versionName = "2.0.2"
        const val composeCompilerVersion = "1.4.0"
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
        const val timber = "com.jakewharton.timber:timber:_"
        const val glide = "com.github.bumptech.glide:glide:_"
        const val youtubePlayer = "com.pierfrancescosoffritti.androidyoutubeplayer:core:10.0.3"
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:_"
        const val coil = "io.coil-kt:coil-compose:_"
        const val jsoup = "org.jsoup:jsoup:_"

        object Android {
            const val appcompat = "androidx.appcompat:appcompat:_"
            const val coreKtx = "androidx.core:core-ktx:_"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:_"
            const val material = "com.google.android.material:material:_"
            const val cardView = "androidx.cardview:cardview:_"
            const val recyclerView = "androidx.recyclerview:recyclerview:_"
            const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:_"
            const val exoplayer = "com.google.android.exoplayer:exoplayer:_"
            const val ossLicenses = "com.google.android.gms:play-services-oss-licenses:_"

            object Navigation {
                const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:_"
                const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:_"
            }

            object Compose {
                const val compiler = "androidx.compose.compiler:compiler:_"
                const val ui = "androidx.compose.ui:ui:_"
                const val material = "androidx.compose.material:material:_"
                const val uiTooling = "androidx.compose.ui:ui-tooling:_"
                const val foundation = "androidx.compose.foundation:foundation:_"
            }
        }

        object Retrofit {
            const val retrofit = "com.squareup.retrofit2:retrofit:_"
            const val adapterRxJava2 = "com.squareup.retrofit2:adapter-rxjava2:_"
            const val converterMoshi = "com.squareup.retrofit2:converter-moshi:_"
            const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:_"
        }

        object Hilt {
            const val hiltAndroid = "com.google.dagger:hilt-android:_"
            const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:_"
            const val hiltCompiler = "androidx.hilt:hilt-compiler:_"
        }

        object Firebase {
            const val bom = "com.google.firebase:firebase-bom:_"
            const val analytics = "com.google.firebase:firebase-analytics:_"
            const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx:_"
        }

        object Rx {
            const val rxJava2 = "io.reactivex.rxjava2:rxjava:_"
            const val rxAndroid = "io.reactivex.rxjava2:rxandroid:_"
            const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:_"
        }

    }

    object TestLibs {
        const val jUnit = "junit:junit:_"
        const val mockk = "io.mockk:mockk:_"
    }

    object AndroidTestLibs {
        const val core = "androidx.test:core:_"
        const val testRunner = "androidx.test:runner:_"
        const val testRules = "androidx.test:rules:_"
        const val extJunit = "androidx.test.ext:junit:_"
        const val extJunitKtx = "androidx.test.ext:junit-ktx:_"

        object Espresso {
            const val core = "androidx.test.espresso:espresso-core:_"
            const val contrib = "androidx.test.espresso:espresso-contrib:_"
        }
    }

    object Modules {
        const val presentationCommon = ":presentation:common"
        const val data = ":data"
        const val domain = ":domain"
        const val featList = ":presentation:list"
        const val featOpenGs = ":presentation:opengs"
        const val featSettings = ":presentation:settings"
        const val featCreate = ":presentation:create"
        const val postData = ":postdata"
        const val navigation = ":navigation"
    }
}
