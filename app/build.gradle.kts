/*
 * Copyright 2016-2020 Open Food Facts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("com.android.application")
    alias(libs.plugins.resourceplaceholders) apply true
    id("org.greenrobot.greendao")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.dokka)
}

fun obtainTestBuildType(): String {
    // To activate  screenshots buildType in IDE; uncomment next line and comment other
    // otherwise the folder androidTestScreenshots is not recognized as a test folder.
    //val result = "screenshots"
    val result = "debug"

    return project.properties.getOrDefault("testBuildType", result) as String
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    // Kotlin coroutines
    implementation(libs.bundles.kotlin.coroutines)

    // Android KTX
    implementation(libs.bundles.android.ktx)

    implementation(libs.bundles.android.lifecycle)

    // AndroidX
    implementation(libs.bundles.androidx)

    // ML Kit barcode Scanner
    implementation(libs.barcode.scanning)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Hilt for Android Testing
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.dagger)
    kaptAndroidTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.dagger.compiler)

    // WorkManager with Hilt
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    // Networking
    implementation(libs.bundles.networking)

    // Logging
    implementation(libs.logcat)

    // Apache commons
    implementation(libs.bundles.apache.commons)

    // Serialization/Deserialization
    implementation(libs.bundles.jackson)

    // Database
    implementation(libs.greendao)

    // Event bus and index
    implementation(libs.eventbus.runtime)
    kapt(libs.eventbus.compiler)

    // Material design
    implementation(libs.material)

    // Image Loading
    implementation(libs.picasso)

    // Image from gallery or camera
    implementation(libs.easyimage)

    // Barcode and QR Scanner
    // TODO: cannot upgrade, requires API 24 or higher
    implementation(libs.zxing.core)

    implementation(libs.zxing.android.embedded) { isTransitive = false }

    // UI Component : ImageView with Zooming
    implementation(libs.photoview)

    // UI Component : Material Drawer
    // https://github.com/mikepenz/MaterialDrawer/commit/3b2cb1db4c3b6afe639b0f3c21c03c1de68648a3
    // TODO: We need minSdk 16 to update
    implementation(libs.materialdrawer)

    // DO NOT UPDATE : RecyclerViewCacheUtil removed, needs rework
    implementation(libs.fastadapter.commons) { artifact { type = "aar" } }

    // UI Component : Font Icons
    // This Font/Icon grouping resists 'bundling' due to (AAR) type
    // specification not being directly supported by Version Catalogs.
    implementation(libs.iconics.core) {
        artifact { type = "aar" }
    }
    implementation(libs.google.material.typeface) {
        isTransitive = false
        artifact { type = "aar" }
    }
    implementation(libs.android.image.cropper)

    // UI Component : Chips Input
    implementation(libs.nachos)

    // Crash analytics
    implementation(libs.bundles.crash.analytics)

    // ShowCaseView dependency
    implementation(libs.showcaseview)

    // SplashScreen on API 31 and higher
    implementation(libs.androidx.core.splashscreen)

    // Unit Testing
    testImplementation(libs.bundles.testing)

    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)

    // Instrumented tests
    androidTestUtil(libs.orchestrator)

    androidTestImplementation(libs.androidx.test.runner) { exclude("junit") }
    androidTestImplementation(libs.androidx.test.rules)

    androidTestImplementation(libs.androidx.test.ext) { exclude("junit") }

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.androidx.test.espresso.web)
    androidTestImplementation(libs.androidx.test.espresso.contrib) {
        exclude(group = "com.android.support", module = "appcompat-v7")
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "design")
        exclude(module = "recyclerview-v7")
    }
    androidTestImplementation(libs.falcon)
    androidTestImplementation(libs.screengrab)
    androidTestImplementation(libs.test.kotlin.coroutines)

    // Jetpack Compose dependencies
    implementation(libs.androidx.ui)

    resourcePlaceholders { files = listOf("xml/shortcuts.xml") }

}

android {
    compileSdk = 33

    testBuildType = obtainTestBuildType()

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    flavorDimensions += listOf("versionCode", "platform")

    defaultConfig {
        applicationId = "openfoodfacts.github.scrachx.openfood"

        minSdk = 21
        targetSdk = 33

        versionCode = 582
        versionName = "3.9.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        ndk.abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

        multiDexEnabled = true

        buildConfigField("String", "WIKIDATA", "\"https://www.wikidata.org/wiki/Special:EntityData/\"")
        buildConfigField("String", "MATOMO_URL", "\"https://analytics.openfoodfacts.org/matomo.php\"")
        buildConfigField("String", "TESTING_HOST", "\"https://world.openfoodfacts.net\"")
    }

    signingConfigs {
        create("release") {
            if (System.getenv("CI_RELEASE")?.toBoolean() == true) { // CI=true is exported by github action
                val storeFilePath = System.getenv("SIGN_STORE_PATH")
                if (storeFilePath != null) {
                    storeFile = file(storeFilePath)
                }
                storePassword = System.getenv("SIGN_STORE_PASSWORD")
                keyAlias = System.getenv("SIGN_KEY_ALIAS")
                keyPassword = System.getenv("SIGN_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isCrunchPngs = true

            // Enables code shrinking, obfuscation, and optimization for only
            // release build type.
            isMinifyEnabled = false
            

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = false
            // Let's try disabling Proguard- teolemon - 21/04/2020
            // useProguard = false
            // proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }

        create("screenshots") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".screenshots"
        }
    }

    productFlavors {

        ///////////////////////
        // FLAVORS

        create("off") {
            dimension = "versionCode"
            applicationId = "openfoodfacts.github.scrachx.openfood"
            if ("true" == System.getenv("CI_RELEASE")) { // CI=true is exported by github action
                applicationId = "org.openfoodfacts.scanner"
            }
            resValue("string", "app_name", "OpenFoodFacts")
            buildConfigField("String", "APP_NAME", "\"Open Food Facts\"")
            buildConfigField("String", "HOST", "\"https://ssl-api.openfoodfacts.org\"")
            buildConfigField("String", "OFOTHERLINKAPP", "\"org.openbeautyfacts.scanner\"")
            buildConfigField("String", "OFWEBSITE", "\"https://world.openfoodfacts.org/\"")
            buildConfigField("String", "STATICURL", "\"https://static.openfoodfacts.org\"")
        }
        create("obf") {
            dimension = "versionCode"
            applicationId = "openfoodfacts.github.scrachx.openbeauty"
            if ("true" == System.getenv("CI_RELEASE")) { // CI=true is exported by github action
                applicationId = "org.openbeautyfacts.scanner"
            }

            resValue("string", "app_name", "OpenBeautyFacts")
            buildConfigField("String", "APP_NAME", "\"Open Beauty Facts\"")
            buildConfigField("String", "HOST", "\"https://ssl-api.openbeautyfacts.org\"")
            buildConfigField("String", "OFOTHERLINKAPP", "\"org.openfoodfacts.scanner\"")
            buildConfigField("String", "OFWEBSITE", "\"https://world.openbeautyfacts.org/\"")
            buildConfigField("String", "STATICURL", "\"https://static.openbeautyfacts.org\"")
            buildConfigField("String", "MATOMO_URL", "\"https://analytics.openfoodfacts.org/matomo.php\"")
        }
        create("opff") {
            dimension = "versionCode"
            applicationId = "org.openpetfoodfacts.scanner"

            resValue("string", "app_name", "OpenPetFoodFacts")
            buildConfigField("String", "APP_NAME", "\"Open Pet Food Facts\"")
            buildConfigField("String", "HOST", "\"https://ssl-api.openpetfoodfacts.org\"")
            buildConfigField("String", "OFOTHERLINKAPP", "\"org.openfoodfacts.scanner\"")
            buildConfigField("String", "OFWEBSITE", "\"https://world.openpetfoodfacts.org/\"")
            buildConfigField("String", "STATICURL", "\"https://static.openpetfoodfacts.org\"")
        }
        create("opf") {
            dimension = "versionCode"
            applicationId = "org.openproductsfacts.scanner"

            resValue("string", "app_name", "OpenProductsFacts")
            buildConfigField("String", "APP_NAME", "\"Open Products Facts\"")
            buildConfigField("String", "HOST", "\"https://ssl-api.openproductsfacts.org\"")
            buildConfigField("String", "OFOTHERLINKAPP", "\"org.openfoodfacts.scanner\"")
            buildConfigField("String", "OFWEBSITE", "\"https://world.openproductsfacts.org/\"")
            buildConfigField("String", "STATICURL", "\"https://static.openproductsfacts.org\"")
            buildConfigField("String", "WIKIDATA", "\"https://www.wikidata.org/wiki/Special:EntityData/\"")
        }

        ///////////////////////
        // PLATFORMS

        create("playstore") {
            dimension = "platform"

            buildConfigField("boolean", "USE_MLKIT", "true")

            dependencies {
                // Google Play Core Dependencies - In-app review system
                implementation("com.google.android.play:core:1.10.3")
                implementation("com.google.android.play:core-ktx:1.8.1")
            }
        }

        create("fdroid") {
            dimension = "platform"

            buildConfigField("boolean", "USE_MLKIT", "false")
        }
    }

    compileOptions {
        // Sets Java compatibility to Java 8
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    lint {
        abortOnError = false

        disable += setOf(
            "MissingTranslation",
            "ImpliedQuantity",
            // Invalid Resource Folder is for values-b+sr… folders
            "InvalidResourceFolder"
        )
    }

    packagingOptions {
        resources.excludes += listOf(
            "META-INF/DEPENDENCIES.txt",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE.txt",
            "META-INF/NOTICE",
            "META-INF/LICENSE",
            "META-INF/DEPENDENCIES",
            "META-INF/notice.txt",
            "META-INF/license.txt",
            "META-INF/dependencies.txt",
            "META-INF/LGPL2.1"
        )
    }

    testOptions {
        unitTests.all { test ->
            test.useJUnitPlatform {
                includeEngines("junit-jupiter", "junit-vintage")
            }
            test.testLogging {
                events("passed", "skipped", "failed")
            }
        }

        // avoid "Method ... not mocked."
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
    namespace = "openfoodfacts.github.scrachx.openfood"
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("eventBusIndex", "openfoodfacts.github.scrachx.openfood.app.OFFEventsIndex")
    }
}

greendao { schemaVersion(22) }
