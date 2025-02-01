import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.compiler)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.privin.mm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.privin.mm"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val properties = Properties()
            val propertiesFile = rootProject.file("signing.properties")

            if (propertiesFile.exists()) {
                properties.load(propertiesFile.inputStream())
                val storeFilePath: String = properties.getProperty("STORE_FILE")?.removeSurrounding("\"")?.trim() ?: ""
                storeFile = file(storeFilePath)
                storePassword = properties.getProperty("STORE_PASSWORD")?.removeSurrounding("\"")?.trim() ?: ""
                keyAlias = properties.getProperty("KEY_ALIAS")?.removeSurrounding("\"")?.trim() ?: ""
                keyPassword = properties.getProperty("KEY_PASSWORD")?.removeSurrounding("\"")?.trim() ?: ""
            }
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            isShrinkResources = false
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    api(project(":core:data"))


    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.room)
    ksp(libs.room.compiler)
    implementation(libs.dagger.hilt.navigation)
    implementation(libs.material.icons)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}