plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
}

android {
    namespace = "com.github.dreamonex.onepage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.dreamonex.onepage"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    /* 统一 Java/Kotlin 目标为 17，解决 JVM target 不一致 */
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }

    buildFeatures { compose = true }

    composeOptions { kotlinCompilerExtensionVersion = "1.6.10" }
}

dependencies {
    /* ---------- Compose 基础 ---------- */
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom); androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.9.0")     // setContent
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-saveable")     // rememberSaveable

    /* ---------- DataStore & 协程 ---------- */
    implementation("androidx.datastore:datastore-preferences:1.0.0")   // stringPreferencesKey
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    /* ---------- Markdown 渲染 (JitPack) ---------- */
    implementation("com.github.jeziellago:compose-markdown:0.5.7")

    /* ---------- MaterialComponents (XML 主题) ---------- */
    implementation("com.google.android.material:material:1.11.0")
}
