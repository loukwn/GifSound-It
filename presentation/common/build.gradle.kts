plugins {
    id("gs-presentation-nonscreen-compose-plugin")
}

dependencies {
    implementation(Config.Libs.Android.material)

    implementation(Config.Libs.Android.Compose.ui)
    implementation(Config.Libs.Android.Compose.material)
    implementation(Config.Libs.Android.Compose.foundation)
    implementation(Config.Libs.Android.Compose.uiTooling)
    implementation(Config.Libs.Android.Compose.compiler)
}
