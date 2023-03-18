plugins {
    id("gs-library-plugin")
    id("kotlin-parcelize")
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

    implementation(Config.TestLibs.mockk)
    testImplementation(Config.TestLibs.jUnit)
}
