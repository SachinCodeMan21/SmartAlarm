plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.smartalarm.testutils" // unique package for this module
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    testOptions{
        unitTests.isIncludeAndroidResources = true
    }

    lint{
        targetSdk = 36
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",        // Exclude license file to prevent duplicate resource conflicts during packaging
                "META-INF/LICENSE-notice.md",  // Exclude license notice file for the same reason,
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        // Enable or disable features as needed
        buildConfig = false // usually not needed here
    }

    // Usually no buildTypes or signingConfigs needed for library modules
}

dependencies {
    implementation(project(":app"))  // Access your app module classes/interfaces

    // Kotlin stdlib (usually included transitively, but explicit is okay)
    implementation(kotlin("stdlib"))

    // Testing libraries you mentioned
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.truth)
    implementation(libs.mockk)

    // Hilt testing support for @TestInstallIn, etc.
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.espresso.idling.resource)
    kapt(libs.hilt.compiler)
}
