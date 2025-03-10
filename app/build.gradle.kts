plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // add this for google services
    alias(libs.plugins.google.services)
}

android {
    namespace = "scisrc.mobiledev.blogexample"
    compileSdk = 35

    defaultConfig {
        applicationId = "scisrc.mobiledev.blogexample"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase BoM (Bill of Materials)
    implementation(platform(libs.firebase.bom))

    // Firebase core features
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)

    // Realtime Database
    implementation(libs.firebase.database)

    // Cloud Firestore
    implementation(libs.firebase.firestore)
}