plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id ("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.caobatruckscontrol"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.caobatruckscontrol"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    implementation(libs.androidx.compose.material)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
  //  implementation(libs.androidx.ui.desktop)
    implementation(libs.play.services.nearby)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
   // implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt ("androidx.hilt:hilt-compiler:1.2.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")



    // Compose dependencies
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.compose.material:material-icons-extended:1.6.4")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.6")

    implementation ("androidx.compose.material:material:1.2.0")

    //implementation ("com.google.android.material3:material:3.5.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation ("com.google.firebase:firebase-auth-ktx:21.0.0")

    implementation("com.google.firebase:firebase-storage")

    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.github.skydoves:landscapist-glide:1.3.7")

    //material core3
    implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.0.2")

    implementation("com.maxkeppeler.sheets-compose-dialogs:calendar:1.0.2")

    implementation("com.maxkeppeler.sheets-compose-dialogs:clock:1.0.2")

//    implementation ("com.google.firebase:firebase-firestore-ktx")
//    implementation ("com.google.firebase:firebase-messaging-ktx")
//    implementation ("com.google.firebase:firebase-messaging:23.0.0")
//
//    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
//    implementation ("org.json:json:20210307")

}

configurations {
    all {
        resolutionStrategy {
            // Force a specific version of a dependency
            force("androidx.compose.material3:material3:1.2.1")

            // Exclude specific transitive dependencies if needed
            // exclude(mapOf("group" to "com.example", "module" to "conflicting-library"))
        }
    }
}



















