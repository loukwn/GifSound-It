plugins {
    id("gs-presentation-view-plugin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.data))
    implementation(project(Config.Modules.presentationCommon))
    implementation(project(Config.Modules.navigation))

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    implementation(Config.Libs.timber)

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)
    implementation(Config.Libs.Android.Navigation.navigationUiKtx)
    implementation(Config.Libs.Android.constraintLayout)
    implementation(Config.Libs.Android.material)

    testImplementation(Config.TestLibs.jUnit)
    testImplementation(Config.TestLibs.mockk)
}
