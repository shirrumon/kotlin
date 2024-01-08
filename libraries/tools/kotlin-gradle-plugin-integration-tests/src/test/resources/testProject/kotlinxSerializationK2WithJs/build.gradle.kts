plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    js {
        binaries.executable()
        browser {}
    }

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "2.0"
            }
        }
    }
}

dependencies {
    "jsMainImplementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}
