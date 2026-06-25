import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

// Loads build-time secrets from secrets.properties (falling back to the checked-in
// secrets.default.properties), replacing the unmaintained Google secrets-gradle-plugin
// which is not compatible with AGP 9.
val secretProperties: Properties by lazy {
    Properties().apply {
        rootProject.file("secrets.default.properties").takeIf { it.exists() }
            ?.inputStream()?.use { load(it) }
        rootProject.file("secrets.properties").takeIf { it.exists() }
            ?.inputStream()?.use { load(it) }
    }
}

fun secret(key: String): String =
    secretProperties.getProperty(key, "").trim().trim('"')

android {
    namespace = "com.privin.network"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL", "\"${secret("BASE_URL")}\"")
        buildConfigField("String", "API_KEY", "\"${secret("API_KEY")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.loggingInterceptor)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi.kotlin)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}