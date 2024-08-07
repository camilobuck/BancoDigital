@file:Suppress("UNUSED_EXPRESSION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.bancodigital"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bancodigital"
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
    kotlinOptions {
        jvmTarget = "1.8"
        viewBinding {
            enable = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    "2.8.3"
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v283)
    implementation(libs.androidx.lifecycle.livedata.ktx.v283)

    implementation(libs.hilt.android.v2481)
    ksp(libs.hilt.android.compiler.v2481)

    // https://github.com/VicMikhailau/MaskedEditText
    implementation(libs.maskededittext)

    //https://github.com/cheonjaeung/shapedimageview
    implementation(libs.shapedimageview)

    //https://github.com/square/picasso
    implementation(libs.picasso)

    // https://github.com/ParkSangGwon/TedPermission
    implementation(libs.tedpermission.normal)


}