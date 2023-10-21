@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.parcelize)
    alias(libs.plugins.jlleitschuh.ktlint)
    id("maven-publish")
}

ktlint {
    version.set(libs.versions.ktlint)
    outputToConsole.set(true)
    verbose.set(true)
}

android {
    namespace = "fi.paytrail.paymentsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        // Sets Java compatibility to Java 17
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }
    android {
        publishing {
            singleVariant("release") {
                withSourcesJar()
            }
        }
    }
}
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.paytrail"
            artifactId = "paytrail-android-sdk"
            version = "v0.1.0-alpha"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {

    api(project(":api-client-retrofit2"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime.livedata)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.html.text)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    testImplementation(libs.coil.test)

    debugImplementation(libs.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Needed for createAndroidComposeRule, but not createComposeRule:
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.okhttp3.mockwebserver)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.kluent.android)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
