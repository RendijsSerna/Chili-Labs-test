plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
}


android {
    namespace = "com.example.project"
    compileSdk = 35

    buildFeatures {
        buildConfig = true  // Enable BuildConfig feature
    }

    defaultConfig {
        buildConfigField("String", "GIPHY_API_KEY", "\"${project.properties["GIPHY_API_KEY"]}\"")
        applicationId = "com.example.project"
        minSdk = 24
        targetSdk = 35
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
        jvmTarget = "11" // Or the appropriate JVM version for your project
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core AndroidX and Compose libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.appcompat)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit for network calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coil for image and GIF loading in Compose
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    implementation(libs.view.model)

    implementation(libs.lottie.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.constraintlayout)



}

