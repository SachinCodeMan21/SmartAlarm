import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hiltPlugin)
    alias(libs.plugins.navigationSafeArgs)
    id("kotlin-kapt")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.10"
}

android {

    namespace = "com.example.smartalarm"
    compileSdk = 36
    buildToolsVersion = "36.0.0"


    defaultConfig {
        applicationId = "com.example.smartalarm"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.smartalarm.runner.HiltTestRunner" // Custom test runner to enable Hilt dependency injection in instrumentation tests

    }

    val localProperties = Properties()  // Create a Properties object to hold key-value pairs
    val localPropertiesFile = File(rootDir, "secret.properties")  // Define the file location for secret.properties in the project root

    // Check if the secret.properties file exists and is a valid file
    if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
        // Open an input stream to read the file and load its contents into localProperties
        localPropertiesFile.inputStream().use {
            localProperties.load(it)
        }
    }



    buildTypes {

        release {
            isMinifyEnabled = false  // Disable code shrinking and obfuscation for release build (can enable for production)

            // Specify ProGuard rules files for code optimization and obfuscation
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Inject a build-time constant (API key) for the production environment, accessible via BuildConfig.GOOGLE_API_KEY_PROD
            buildConfigField(
                "String",
                "GOOGLE_API_KEY_PROD",
                localProperties.getProperty("GOOGLE_API_KEY_PROD")
            )
        }

        debug {
            // Inject a build-time constant (API key) for the debug environment, accessible via BuildConfig.GOOGLE_API_KEY
            buildConfigField(
                "String",
                "GOOGLE_API_KEY",
                localProperties.getProperty("GOOGLE_API_KEY")
            )

            getByName("debug") {
                enableUnitTestCoverage = true
                enableAndroidTestCoverage = true
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
    

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
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
}

dependencies {

    // ────── 📱 PRODUCTION DEPENDENCIES ──────

    // Core Android & UI
    implementation(libs.androidx.core.ktx) // Kotlin extensions for Android core APIs
    implementation(libs.androidx.appcompat) // AppCompat for backward compatibility with older Android versions
    implementation(libs.google.material) // Google Material Design UI components (e.g., Buttons, FAB, Navigation)
    implementation(libs.androidx.activity) // Lifecycle-aware Activity and ActivityResult APIs
    implementation(libs.androidx.constraintlayout) // Layout system for building responsive UIs with constraints
    implementation(libs.androidx.fragment) // Fragment management and utilities


    // Lifecycle & Architecture Components
    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData with coroutine extensions
    implementation(libs.androidx.lifecycle.service) // Lifecycle-aware services (e.g., LifecycleService)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.uiautomator) // ViewModel support with Kotlin coroutines
    kapt(libs.androidx.lifecycle.compiler) // Annotation processor for Lifecycle (e.g., @OnLifecycleEvent)


    // Android Splash API
    implementation(libs.androidx.core.splashscreen)


   // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx) // Provides NavController, Safe Args, and Kotlin extensions for fragment-based navigation
    implementation(libs.androidx.navigation.ui.ktx)       // Integrates navigation UI components (e.g., BottomNavigationView, Toolbar) with NavController, with Kotlin extensions


    // Coroutines
    implementation(libs.kotlinx.coroutines.android) // Kotlin Coroutines support for Android, enabling asynchronous programming on main and background threads


    // Room (Database)
    implementation(libs.androidx.room.runtime) // Core runtime for Room persistence library
    implementation(libs.androidx.room.ktx) // Kotlin extensions for Room including Coroutines and Flow support
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.compiler) // Annotation processor for Room (generates database code)


    // Retrofit & Gson (Networking & Serialization)
    implementation(libs.retrofit) // Retrofit: Type-safe HTTP client for networking
    implementation(libs.converter.gson) // Retrofit converter for JSON serialization/deserialization using Gson
    implementation(libs.gson) // Gson: JSON parsing library used by Retrofit converter
    implementation(libs.logging.interceptor) // OkHttp interceptor for logging HTTP request and response details

    // Google Places API
    implementation(libs.places) // Provides APIs for location and place data, autocomplete, and more

    // ShawnLin Picker
    implementation(libs.number.picker) // Custom number picker UI component for selecting numbers easily i.e Provide [NumPicker]

    // WorkManager (Background tasks)
    implementation(libs.androidx.work.runtime.ktx) // Simplifies scheduling deferrable, asynchronous background work


   // Hilt (Dependency Injection)
    implementation(libs.hilt.android) // Core Hilt library for Android DI
    kapt(libs.hilt.android.compiler) // Annotation processor for Hilt code generation


   // Hilt integration with WorkManager
    implementation(libs.androidx.hilt.work.v110) // Enables injecting dependencies into WorkManager workers
    kapt(libs.androidx.hilt.compiler) // Compiler for Hilt integration with AndroidX components




    // ────── 🧪 TEST DEPENDENCIES ──────


    // Unit Testing & Instrumented Android Testing
    testImplementation(libs.junit) // JUnit 4 - Local JVM unit tests
    androidTestImplementation(libs.androidx.test.junit) // AndroidX JUnit - Integration with Android instrumented tests (ActivityScenario, etc.)
    androidTestImplementation(libs.androidx.test.core.ktx) // Core Android testing APIs with Kotlin extensions for instrumented tests

    //Runner
    androidTestImplementation(libs.androidx.test.runner) // Runs Android instrumentation tests
    androidTestImplementation(libs.androidx.test.rules)  // Provides testing rules for Android tests (e.g., ActivityScenarioRule)

    // Coroutine Testing
    testImplementation(libs.kotlinx.coroutines.test) // For testing coroutines in unit tests (e.g., TestScope, runTest, TestDispatcher)
    androidTestImplementation(libs.kotlinx.coroutines.test) // For coroutine testing in Android instrumented tests


    // Architecture Component Testing (LiveData, ViewModel, etc.)
    testImplementation(libs.androidx.core.testing) // Provides TestCoroutineDispatcher, InstantTaskExecutorRule, etc. for unit tests
    androidTestImplementation(libs.androidx.core.testing) // Same tools for instrumented Android tests (e.g., LiveData testing)


    // Mocking libraries for unit and instrumentation tests
    testImplementation(libs.mockk) // MockK core for unit tests
    androidTestImplementation(libs.mockk.android) // MockK Android extensions for instrumentation tests


    // Android Instrumentation Tests with Espresso
    androidTestImplementation(libs.androidx.espresso.core)       // Espresso core for UI testing basic interactions like clicks, typing, and assertions
    androidTestImplementation(libs.androidx.espresso.contrib)    // Espresso contrib provides additional UI components and helpers (e.g., RecyclerView actions, Drawer actions)
    androidTestImplementation(libs.androidx.espresso.intents)    // Espresso intents for validating and stubbing Android intents during UI tests
    androidTestImplementation(libs.androidx.fragment.testing)


    // StateFlow / Flow Testing
    testImplementation(libs.turbine) // Lightweight Flow testing library: collect emissions, assert values, handle completion/errors in tests


    // Assertion libraries for unit and instrumentation tests
    testImplementation(libs.truth) // Fluent assertions for unit test readability
    androidTestImplementation(libs.truth) // Fluent assertions for instrumentation tests
    androidTestImplementation(libs.hamcrest) // Flexible matcher assertions for instrumentation tests


    // Navigation Testing - Provides testing utilities for navigation components in instrumentation tests
    androidTestImplementation(libs.androidx.navigation.testing)

    // Hilt Testing - Supports dependency injection testing with Hilt in instrumentation tests
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)  // Annotation processor for Hilt in Android tests

    testImplementation(project(":test-utils"))       // Use shared test helpers/fakes in unit tests
    androidTestImplementation(project(":test-utils")) // Use shared test helpers/fakes in instrumentation tests
    testImplementation(kotlin("test"))


}

