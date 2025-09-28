plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Applies the Kotlin Compose plugin for Jetpack Compose support
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.czy.ttu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.czy.ttu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // Material Design 3 components for Compose
    implementation(libs.androidx.material3)
    // Core Kotlin extensions (duplicate)
    implementation(libs.androidx.core.ktx)
    // Lifecycle runtime Kotlin extensions
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Jetpack Compose integration with activities
    implementation(libs.androidx.activity.compose)
    // Bill of Materials (BOM) for Jetpack Compose, manages versions of Compose libraries
    implementation(platform(libs.androidx.compose.bom))
    // Core Jetpack Compose UI library
    implementation(libs.androidx.compose.ui)
    // Jetpack Compose graphics library
    implementation(libs.androidx.compose.ui.graphics)
    // Jetpack Compose tooling for previewing UIs
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Material Design 3 components for Compose (duplicate)
    implementation(libs.androidx.compose.material3)
    // Jetpack Compose Navigation library
    implementation(libs.navigation.compose)
    // Material Design components (older version, consider if still needed)
    implementation(libs.material.v1100)
    // Extended Material Icons for Jetpack Compose
    implementation(libs.androidx.compose.material.icons)
    // Provides compatibility for older Android versions (duplicate)
    implementation(libs.androidx.appcompat)
    // Android-specific layout components for Jetpack Compose Foundation
    implementation(libs.androidx.foundation.layout.android)
    // Android-specific components for Jetpack Compose Foundation
    implementation(libs.androidx.foundation.android)
    // For better JSON handling
    implementation(libs.kotlinx.serialization.json)
    // For animations
    implementation(libs.androidx.compose.animation)
    // For better camera handling (CameraX)
    implementation(libs.androidx.camera.extensions) // CameraX Extensions
    // For better permissions handling
    implementation(libs.accompanist.permissions)
    // For Jetpack Compose Foundation (core building blocks, theming, and layout system)
    implementation(libs.androidx.compose.foundation)
    // CameraX Lifecycle
    implementation(libs.camera.lifecycle)
    // CameraX Camera2 implementation
    implementation(libs.camera.camera2)
    // CameraX View utilities
    implementation(libs.camera.view)
    // CameraX Lifecycle (appears to be an alternative alias or duplicate)
    implementation(libs.x.androidx.camera.lifecycle)
    // CameraX Camera2 implementation (appears to be an alternative alias or duplicate)
    implementation(libs.x.androidx.camera.camera2)
    // CameraX View utilities (appears to be an alternative alias or duplicate)
    implementation(libs.x.androidx.camera.view)
    // TensorFlow Lite for on-device machine learning
    implementation(libs.tensorflow.lite)
    // TensorFlow Lite support library
    implementation(libs.tensorflow.lite.support)
    // TensorFlow Lite GPU delegate
    implementation(libs.tensorflow.lite.gpu)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Jetpack Compose testing BOM (manages versions for Compose test libraries)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    // Jetpack Compose UI testing library for JUnit4
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // Jetpack Compose UI tooling for debugging
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Jetpack Compose UI test manifest for debugging
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}