plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "tn.esprit.eventsphere"
    compileSdk = 34

    defaultConfig {
        applicationId = "tn.esprit.eventsphere"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // Core Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Room Dependencies
    implementation("androidx.room:room-runtime:2.5.0") // Room runtime
    annotationProcessor("androidx.room:room-compiler:2.5.0") // Room compiler for annotation processing

    // For Kotlin users, use kapt instead of annotationProcessor
    // kapt("androidx.room:room-compiler:2.5.0")

    // Optional: If you are using Kotlin coroutines, add Room KTX
    implementation("androidx.room:room-ktx:2.5.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
