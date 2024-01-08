plugins {
    kotlin("multiplatform") version "<pluginMarkerVersion>"
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }
}
kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("com.example:lib-2")
                implementation(npm("node-fetch", "3.2.8"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
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