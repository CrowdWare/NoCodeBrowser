import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// read local.properties file
val localPropsFile = file("../local.properties")
val localProps = Properties()
localProps.load(localPropsFile.inputStream())

// increment versionCode
val currentVersionCode = localProps["VERSION_CODE"].toString().toInt()
val newVersionCode = currentVersionCode + 1
localProps["VERSION_CODE"] = newVersionCode.toString()

// write new versionCode back to the file
localProps.store(localPropsFile.outputStream(), null)

// masterpiece in version numbering ;-)
// version will increment all 10 minutes
val currentDateTime: LocalDateTime = LocalDateTime.now()
val majorVersion = (currentDateTime.year - 2014) / 10
val yearPart = (currentDateTime.year - 2014) - 10
val monthPart = String.format("%02d", currentDateTime.monthValue)
val dayPart = String.format("%02d", currentDateTime.dayOfMonth)
val hourPart = String.format("%02d", currentDateTime.hour)
val minutesPart = String.format("%02d", currentDateTime.minute)
val version = "$majorVersion.$yearPart$monthPart.$dayPart$hourPart$minutesPart".take(11)

android {
    namespace = "at.crowdware.nocodebrowser"
    compileSdk = 34

    sourceSets {
        // Configure the `main` source set to include the custom directory
        getByName("main") {
            kotlin.srcDir(layout.buildDirectory.dir("generated/version"))
        }
    }

    defaultConfig {
        androidResources {
            ignoreAssetsPattern += listOf(
                "!.svn",
                "!.git",
                "!.gitignore",
                "!.ds_store",
                "!*.scc",
                "<dir>_*",
                "!CVS",
                "!thumbs.db",
                "!picasa.ini",
                "!*~"
            )
        }
        applicationId = "at.crowdware.nocodebrowser"
        minSdk = 29
        targetSdk = 34
        versionCode = newVersionCode
        versionName = "$version"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(project(":nocodelibmobile"))

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    // rest client
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // grammar for parsing SML
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")

    // video player
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")

    // youtube player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // filament 3D
    implementation("com.google.android.filament:filament-android:1.54.5")
    implementation("com.google.android.filament:filament-utils-android:1.54.5")
    implementation("com.google.android.filament:gltfio-android:1.54.5")
}

tasks.named("assemble") {
    mustRunAfter("generateVersionFile")
}

tasks.register("generateVersionFile") {
    val outputDir = layout.buildDirectory.dir("generated/version").get().asFile
    val versionValue = version // Assuming `version` is a valid property in your project

    inputs.property("version", versionValue)
    outputs.dir(outputDir)

    doLast {
        // Write the version number to the generated file
        val versionFile = outputDir.resolve("Version.kt")
        versionFile.parentFile.mkdirs() // Create the directory if it doesn't exist
        versionFile.writeText("""
            package at.crowdware.nocodebrowser

            object Version {
                const val version = "$versionValue"
            }
        """.trimIndent())
        println("Version changed to: $versionValue")
    }
}

// Ensure that generateVersionFile is run before compileDebugKotlin
tasks.withType<KotlinCompile> {
    dependsOn("generateVersionFile")
    inputs.dir(layout.buildDirectory.dir("generated/version"))
}
tasks.named("build") {
    dependsOn("generateVersionFile")
}
