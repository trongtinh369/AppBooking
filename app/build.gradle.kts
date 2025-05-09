plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services") // Google Services Plugin
}

android {
    namespace = "com.example.booktourdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.booktourdemo"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }

    // Thêm dòng này nếu Gradle báo lỗi không tìm thấy thư viện trong libs
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Dùng bản cũ hơn
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")


    //upload (ảnh từ máy)
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    // DatetimePicker
    implementation ("com.google.android.material:material:1.12.0")
    // Map voi location
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    
    // Jetpack Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    // Material Components cho BottomNavigationView
    implementation ("com.google.android.material:material:1.12.0")

    // Thêm dependency cho Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation ("com.google.firebase:firebase-firestore-ktx") // firebase firestore

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4") // để gọi await() với Firebase

    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)

    // Facebook SDK đầy đủ
    implementation ("com.facebook.android:facebook-android-sdk:15.2.0")

    // libs zalo pay
    //  Thêm AAR từ thư mục libs
    implementation(files("libs/zpdk-release-v3.1.aar"))
    // Glide (hiển thị ảnh từ URL)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\ACER\\Desktop\\ZaloPaylib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    kapt("com.github.bumptech.glide:compiler:4.16.0")




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // zalo
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
}