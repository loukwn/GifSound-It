plugins {
    id("gs-application-plugin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.featList))
    implementation(project(Config.Modules.featOpenGs))
    implementation(project(Config.Modules.featSettings))
    implementation(project(Config.Modules.featCreate))
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
