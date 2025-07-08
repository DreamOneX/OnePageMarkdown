// settings.gradle.kts

pluginManagement {
    plugins {
        id("com.android.application") version "8.5.0"
        kotlin("android")               version "2.0.0"
        kotlin("plugin.compose")        version "2.0.0"
    }
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    // 统一管理所有模块的依赖仓库
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")  // ← 加上这一行
    }
}

rootProject.name = "OnePageMarkdown"
include(":app")
