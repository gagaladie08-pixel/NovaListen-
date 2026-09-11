// app/build.gradle.kts

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// Fonction pour lire local.properties sans exposer les clés
fun getLocalProperty(key: String): String {
    val props = Properties()
    val file  = rootProject.file("local.properties")
    if (file.exists()) props.load(file.inputStream())
    return props.getProperty(key, "")
}

android {
    namespace   = "com.novalisten.app"
    compileSdk  = 35

    defaultConfig {
        applicationId   = "com.novalisten.app"
        minSdk          = 26
        targetSdk       = 35
        versionCode     = 1
        versionName     = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SPOTIFY_CLIENT_ID",    "\"${getLocalProperty("SPOTIFY_CLIENT_ID")}\"")
        buildConfigField("String", "LASTFM_API_KEY",       "\"${getLocalProperty("LASTFM_API_KEY")}\"")
        buildConfigField("String", "FANART_API_KEY",       "\"${getLocalProperty("FANART_API_KEY")}\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"${getLocalProperty("SPOTIFY_REDIRECT_URI")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled     = true
            isShrinkResources   = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled     = false
            applicationIdSuffix = ".debug"
            versionNameSuffix   = "-DEBUG"
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
        compose     = true
        buildConfig = true
    }

    // ✅ Room schema export
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    // ── Core ──────────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)

    // ── Compose ───────────────────────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)

    // ── Hilt ──────────────────────────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // ── Room ──────────────────────────────────────────────────────────────
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // ── Réseau ────────────────────────────────────────────────────────────
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // ── Coroutines ────────────────────────────────────────────────────────
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // ── Sécurité ──────────────────────────────────────────────────────────
    implementation(libs.security.crypto)

    // ── WorkManager ───────────────────────────────────────────────────────
    implementation(libs.work.runtime)

    // ── DataStore ─────────────────────────────────────────────────────────
    implementation(libs.datastore.preferences)

    // ── Images ────────────────────────────────────────────────────────────
    implementation(libs.coil.compose)

    // ── Tests ─────────────────────────────────────────────────────────────
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.test.core)
}