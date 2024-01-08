plugins {
    kotlin("multiplatform")
}

repositories {
    mavenLocal()
    mavenCentral()
}

val kotlin_version: String by extra

kotlin {
    js {
        binaries.executable()
        nodejs()
    }

    sourceSets {
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js:$kotlin_version")
            }
        }
    }
}