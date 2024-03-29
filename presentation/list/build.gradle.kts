plugins {
    id("gs-presentation-view-plugin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.domain))
    implementation(project(Config.Modules.navigation))
    implementation(project(Config.Modules.postData))
    implementation(project(Config.Modules.presentationCommon))

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.cardView)
    implementation(Config.Libs.Android.constraintLayout)
    implementation(Config.Libs.Android.recyclerView)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)
    implementation(Config.Libs.Android.swipeRefreshLayout)
    implementation(Config.Libs.Android.material)

    implementation(Config.Libs.glide)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    implementation(Config.Libs.timber)

    testImplementation(Config.TestLibs.mockk)
    testImplementation(Config.TestLibs.jUnit)
}
