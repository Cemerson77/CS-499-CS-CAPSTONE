import org.bouncycastle.its.asn1.EndEntityType.app
import java.util.regex.Pattern.compile

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.christineemersonweighttrackingapp"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.christineemersonweighttrackingapp"
        minSdk = 28
        targetSdk = 33
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

    //buildFeatures{
    // viewBinding true
    // }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        compile ("com.android.support:design:24.1.1") // design support Library

    }
}


dependencies {
    implementation ("androidx.navigation:navigation-fragment:2.3.5")
    implementation ("androidx.navigation:navigation-ui:2.3.5")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-common:20.3.3")
    implementation("com.google.firebase:firebase-inappmessaging:20.3.5")
    implementation("com.jjoe64:graphview:4.2.2")
    implementation ("androidx.recyclerview:recyclerview:1.3.1")
    implementation ("com.google.android.material:material:1.3.0-alpha03")
    implementation ("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}