import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        browser {
        }
        binaries.executable()
        val main by compilations.getting
        main.binaries
            .matching { it.mode == KotlinJsBinaryMode.DEVELOPMENT }
            .matching { it is JsIrBinary }
            .all  {
                this as JsIrBinary
                linkTask.configure {
                    val rootCacheDir = rootCacheDirectory.get()
                    rootCacheDirectory.set(rootCacheDir)
                }
            }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":lib"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}