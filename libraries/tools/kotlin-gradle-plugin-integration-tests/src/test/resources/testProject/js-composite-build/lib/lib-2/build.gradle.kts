plugins {
    kotlin("multiplatform")
}

group = "com.example"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        nodejs()
        browser()

        sourceSets {
            val jsMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                    implementation("com.example:base2")
                    implementation(npm("async", "2.6.2"))
                }
            }
        }
    }
}

tasks.named("jsBrowserTest") {
    enabled = false
}

rootProject.tasks
    .withType(org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask::class.java)
    .named("kotlinNpmInstall")
    .configure {
        args.addAll(
            listOf(
                "--network-concurrency",
                "1",
                "--mutex",
                "network"
            )
        )
    }