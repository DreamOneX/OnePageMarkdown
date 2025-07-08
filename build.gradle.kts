plugins {
    // 只声明，不真正应用到根项目，用 apply false
    id("com.android.application") version "8.5.0" apply false
    kotlin("android")               version "2.0.0" apply false
    kotlin("plugin.compose")        version "2.0.0" apply false
}
