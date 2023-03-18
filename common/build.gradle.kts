plugins {
    id("gs-presentation-nonscreen-compose-plugin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Config.Libs.Android.appcompat)
    implementation(Config.Libs.Android.material)

    implementation(Config.Libs.Android.Compose.ui)
    implementation(Config.Libs.Android.Compose.material)
    implementation(Config.Libs.Android.Compose.foundation)
    implementation(Config.Libs.Android.Compose.uiTooling)
    implementation(Config.Libs.Android.Compose.compiler)

    implementation(Config.Libs.Hilt.hiltAndroid)
    kapt(Config.Libs.Hilt.hiltAndroidCompiler)
}
