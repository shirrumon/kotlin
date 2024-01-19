plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        useCommonJs()
        binaries.executable()
        nodejs {
        }
    }
}

dependencies {
    "jsMainImplementation"("org.jetbrains.kotlin:kotlin-stdlib-js")
    "jsTestImplementation"("org.jetbrains.kotlin:kotlin-test-js")
}