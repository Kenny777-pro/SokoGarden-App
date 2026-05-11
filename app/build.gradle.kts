plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.sokogardenapp_kenny"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.sokogardenapp_kenny"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    loopj dependency implementation
    implementation("com.loopj.android:android-async-http:1.4.11")

//    glide dependency implementation
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Volley for networking
    implementation(libs.volley)

    // Google Play Services for Location
    implementation("com.google.android.gms:play-services-location:21.3.0")
}
