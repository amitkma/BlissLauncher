package foundation.e.blisslauncher.buildsrc

object Versions {
    const val android_gradle_plugin = "7.0.4"
    const val dexcount_gradle_plugin = "2.0.0"
    const val kotlin = "1.6.10"
    const val timber = "4.7.1"
    const val junit = "4.12"
    const val ktlint = "0.43.2"
    const val robolectric = "4.3"
    const val mockK = "1.9.3"
    const val firebase_core = "17.1.0"
    const val crashlytics = "2.10.1"
    const val google_services = "4.3.0"
    const val fabric = "1.31.0"
    const val okhttp = "4.1.0"
    const val retrofit = "2.6.1"
    const val dagger = "2.24"
    const val rxjava = "2.2.11"
    const val rxandroid = "2.1.1"
    const val rxkotlin = "2.4.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    const val dexcountGradlePlugin = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${Versions.dexcount_gradle_plugin}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val junit = "junit:junit:${Versions.junit}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"

    object Google {
        const val firebaseCore = "com.google.firebase:firebase-core:${Versions.firebase_core}"
        const val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
        const val gmsGoogleServices = "com.google.gms:google-services:${Versions.google_services}"
        const val fabricPlugin = "io.fabric.tools:gradle:${Versions.fabric}"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0-rc01"
        const val collection = "androidx.collection:collection-ktx:1.1.0"
        const val palette = "androidx.palette:palette:1.0.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0-beta03"

        object Navigation {
            private const val version = "2.2.0-alpha01"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Fragment {
            private const val version = "1.2.0-alpha02"
            const val fragment = "androidx.fragment:fragment:$version"
            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
        }

        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
            const val espressoContrib = "androidx.test.espresso:espresso-contrib:3.2.0"
            const val espressoIntents = "androidx.test.espresso:espresso-intents:3.2.0"
            const val archCoreTesting = "androidx.arch.core:core-testing:2.0.1"

        }

        const val preference = "androidx.preference:preference:1.1.0-rc01"

        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"

        const val coreKtx = "androidx.core:core-ktx:1.2.0-alpha03"

        object Lifecycle {
            private const val version = "2.2.0-alpha03"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Room {
            private const val version = "2.2.0-beta01"
            const val common = "androidx.room:room-common:$version"
            const val runtime = "androidx.room:room-runtime:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val testing = "androidx.room:room-testing:$version"
        }
    }

    object RxJava {
        const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    }

    object Dagger {
        const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
        const val androidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val androidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    object Retrofit {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofit_rxjava_adapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    }

    object OkHttp {
        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    }
}

enum class ReleaseType(private val level: Int) {
    SNAPSHOT(0),
    DEV(1),
    ALPHA(10),
    BETA(20),
    RC(60),
    RELEASE(100);

    fun isEqualOrMoreStableThan(other: ReleaseType): Boolean = level >= other.level

    fun isLessStableThan(other: ReleaseType): Boolean = level < other.level
}

object DependencyUpdates {
    private val stableKeywords = arrayOf("RELEASE", "FINAL", "GA")
    private val releaseRegex = "^[0-9,.v-]+(-r)?$".toRegex(RegexOption.IGNORE_CASE)
    private val rcRegex = releaseKeywordRegex("rc")
    private val betaRegex = releaseKeywordRegex("beta")
    private val alphaRegex = releaseKeywordRegex("alpha")
    private val devRegex = releaseKeywordRegex("dev")

    @JvmStatic
    fun versionToRelease(version: String): ReleaseType {
        val stableKeyword = stableKeywords.any { version.toUpperCase().contains(it) }
        if (stableKeyword) return ReleaseType.RELEASE

        return when {
            releaseRegex.matches(version) -> ReleaseType.RELEASE
            rcRegex.matches(version) -> ReleaseType.RC
            betaRegex.matches(version) -> ReleaseType.BETA
            alphaRegex.matches(version) -> ReleaseType.ALPHA
            devRegex.matches(version) -> ReleaseType.DEV
            else -> ReleaseType.SNAPSHOT
        }
    }

    private fun releaseKeywordRegex(keyword: String): Regex {
        return "^[0-9,.v-]+(-$keyword[0-9]*)$".toRegex(RegexOption.IGNORE_CASE)
    }
}
