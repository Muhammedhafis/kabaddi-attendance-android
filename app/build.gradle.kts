plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.kabaddiattendance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kabaddiattendance"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.4" }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Supabase‑kt
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.1.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.1.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.1.1")

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.6.0")
}
