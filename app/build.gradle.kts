plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.pro.electronic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pro.electronic"
        minSdk = 23
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
    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
}

dependencies {


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.glide)
    implementation(libs.material.dialogs)
    implementation(libs.circleimageview)
    implementation(libs.circleindicator)
    implementation(libs.gson)
    implementation(libs.room)
    annotationProcessor(libs.room.compiler)
    implementation(libs.eventbus)
    implementation(libs.mail)
    implementation(libs.mail.activation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}