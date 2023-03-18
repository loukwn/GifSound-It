plugins {
    id("gs-presentation-compose-plugin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(Config.Modules.common))
    implementation(project(Config.Modules.navigation))

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
    kapt(Config.Libs.Hilt.hiltCompiler)

    implementation(Config.Libs.Rx.rxJava2)
    implementation(Config.Libs.Rx.rxAndroid)

    implementation(Config.Libs.timber)

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.Navigation.navigationFragmentKtx)
//    implementation(Config.Libs.Android.Navigation.navigationUiKtx)
//    implementation(Config.Libs.Android.constraintLayout)
    implementation(Config.Libs.Android.material)

    implementation(Config.Libs.coil)
    implementation(Config.Libs.jsoup)

    implementation(Config.Libs.Android.Compose.ui)
    implementation(Config.Libs.Android.Compose.material)
    implementation(Config.Libs.Android.Compose.foundation)
    implementation(Config.Libs.Android.Compose.uiTooling)
    implementation(Config.Libs.Android.Compose.compiler)

    testImplementation(Config.TestLibs.jUnit)
    testImplementation(Config.TestLibs.mockk)
}
