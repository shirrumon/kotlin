plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        browser {}
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":lib"))
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile>().configureEach {
    friendPaths.from(project(":lib").buildDir.resolve("libs/lib-js.klib"))
}