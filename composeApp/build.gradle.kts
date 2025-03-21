import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.tasks.bundling.Jar

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()
    
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        jvmMain.dependencies {
        }
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.driver.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            
            // SQLDelight dependencies
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.driver.native)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.pettermahlen.sikp13"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.pettermahlen.sikp13"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.pettermahlen.sikp13.db")
            version = 1
            schemaOutputDirectory.set(file("build/dbs"))
            migrationOutputDirectory.set(file("build/migrations"))
        }
    }
}

tasks.register<Jar>("fatJar") {
    group = "build"
    manifest { attributes["Main-Class"] = "com.pettermahlen.sikp13.GenerateEncryptedListKt" }
    val jvmTarget = kotlin.targets.getByName("jvm")
    val compilation = jvmTarget.compilations.getByName("main")
    from(compilation.output.allOutputs)
    compilation.runtimeDependencyFiles?.let { runtimeDeps ->
        from(runtimeDeps.map { if (it.isDirectory) it else zipTree(it) })
    }
    archiveClassifier.set("standalone")
    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE
}

