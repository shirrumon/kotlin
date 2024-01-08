import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        val compilation = compilations.getByName("main")
        org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec.create(compilation, "checkConfigDevelopmentWebpack") {
            inputFileProperty.set(provider { compilation.npmProject.require("webpack/bin/webpack.js") }.map { RegularFile { File(it) } })
            dependsOn("jsBrowserDevelopmentWebpack")
            args("configtest")
            val configFile = tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserDevelopmentWebpack").flatMap { it.configFile }

            doFirst {
                args(configFile.get().absolutePath)
            }
        }
        org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec.create(compilation, "checkConfigProductionWebpack") {
            inputFileProperty.set(provider { compilation.npmProject.require("webpack/bin/webpack.js") }.map { RegularFile { File(it) } })
            dependsOn("jsBrowserProductionWebpack")
            val configFile = tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserProductionWebpack").flatMap { it.configFile }

            args("configtest")
            doFirst {
                args(configFile.get().absolutePath)
            }
        }
        org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec.create(compilation, "checkConfigDevelopmentRun") {
            inputFileProperty.set(provider { compilation.npmProject.require("webpack/bin/webpack.js") }.map { RegularFile { File(it) } })
            dependsOn("jsBrowserDevelopmentRun")
            val configFile = tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserDevelopmentRun").flatMap { it.configFile }
            args("configtest")
            doFirst {
                args(configFile.get().absolutePath)
            }
        }
        org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec.create(compilation, "checkConfigProductionRun") {
            inputFileProperty.set(provider { compilation.npmProject.require("webpack/bin/webpack.js") }.map { RegularFile { File(it) } })
            dependsOn("jsBrowserProductionRun")
            val configFile = tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("jsBrowserProductionRun").flatMap { it.configFile }
            args("configtest")
            doFirst {
                args(configFile.get().absolutePath)
            }
        }
        binaries.executable()
        browser {
            webpackTask {
                generateConfigOnly = true
            }
            runTask {
                generateConfigOnly = true
            }
        }
    }
}

dependencies {
    "jsMainImplementation"(kotlin("stdlib-js"))
}