import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

plugins {
    kotlin("multiplatform") version "1.7.21"
    `java-library`
    `maven-publish`
}

group = "com.geno1024.cinterops"
version = "0.1-SNAPSHOT"

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

java {
    withSourcesJar()
}

publishing {
    repositories {
        mavenLocal {
            name = "GenoLocal"
            url = uri("/srv/http/geno1024/m2repo")
        }
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = System.getenv("ossrh_username")
                password = System.getenv("ossrh_password")
            }
        }
    }
}
