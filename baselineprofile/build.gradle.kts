plugins {
    id("androidx.baselineprofile")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.github.dreamonex.onepage.baselineprofile"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }

    targetProjectPath = ":app"

    testOptions.managedDevices.devices {
        create("pixel6Api34", com.android.build.api.dsl.ManagedVirtualDevice::class) {
            device = "Pixel 6"
            apiLevel = 34
            systemImageSource = "google"
        }
    }
}

dependencies {
    implementation("androidx.benchmark:benchmark-macro-junit4:1.2.2")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0-alpha02")
}