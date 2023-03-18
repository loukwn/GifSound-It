import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("gs-application-plugin") {
            id = "gs-application-plugin"
            implementationClass = "com.loukwn.gifsoundit.GsApplicationGradlePlugin"
        }
        register("gs-presentation-compose-plugin") {
            id = "gs-presentation-compose-plugin"
            implementationClass = "com.loukwn.gifsoundit.GsPresentationComposeGradlePlugin"
        }
        register("gs-presentation-nonscreen-compose-plugin") {
            id = "gs-presentation-nonscreen-compose-plugin"
            implementationClass = "com.loukwn.gifsoundit.GsPresentationNonScreenComposeGradlePlugin"
        }
        register("gs-presentation-view-plugin") {
            id = "gs-presentation-view-plugin"
            implementationClass = "com.loukwn.gifsoundit.GsPresentationViewGradlePlugin"
        }
        register("gs-library-plugin") {
            id = "gs-library-plugin"
            implementationClass = "com.loukwn.gifsoundit.GsLibraryGradlePlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    // Hilt was throwing an error without this. See: https://github.com/google/dagger/issues/3068
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
}