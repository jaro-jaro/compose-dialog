import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
//import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    `maven-publish`
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.jaro-jaro"
            artifactId = "compose-dialog"
            version = "1.2.6"

//            afterEvaluate {
//                from(components["release"])
//            }
        }
    }
    repositories {
        maven {
            name = "compose-dialog"
            url = uri("${project.projectDir}/repo")
        }
    }
}


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs()
    js(KotlinJsCompilerType.IR) {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.foundation)
        }
    }
}

android {
    namespace = "cz.jaro.compose_dialog"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}