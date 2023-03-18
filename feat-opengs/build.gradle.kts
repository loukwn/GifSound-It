plugins {
    id("gs-presentation-view-plugin")
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
    implementation(Config.Libs.Android.exoplayer)

    implementation(Config.Libs.glide)

    implementation(Config.Libs.timber)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    api(Config.Libs.youtubePlayer)

    testImplementation(Config.TestLibs.mockk)
    testImplementation(Config.TestLibs.jUnit)
}
