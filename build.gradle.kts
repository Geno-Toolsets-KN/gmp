import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

plugins {
    kotlin("multiplatform") version "1.6.10"
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64()
//    mingwX64()

    targets.filterIsInstance<KotlinNativeTargetWithHostTests>().forEach {
        it.compilations.getByName("main") {
            val gmp by cinterops.creating {
                packageName = "cinterops.gmp"
            }
        }
    }
}
