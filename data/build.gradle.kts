import java.util.Properties

val localProps = Properties().also { props ->
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
}

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "tmh.learn.weathercompose.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        val apiKey = localProps.getProperty("OPENWEATHER_API_KEY") ?: "MISSING_API_KEY"
        buildConfigField("String", "OPENWEATHER_API_KEY", $"\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.play.services.location)
    implementation(libs.koin.android)
}
