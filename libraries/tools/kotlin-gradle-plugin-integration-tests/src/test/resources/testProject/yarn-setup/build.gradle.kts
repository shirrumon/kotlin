import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnSetupTask
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    kotlin("multiplatform")
}

group = "com.example"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        nodejs()
    }
}

yarn

tasks {
    val yarnFolderRemove by registering {
        doLast {
            yarn.installationDir.deleteRecursively()
        }
    }

    val yarnFolderCheck by registering {
        dependsOn(getByName("kotlinYarnSetup"))

        doLast {
            if (!yarn.installationDir.exists()) {
                throw GradleException()
            }
        }
    }

    val yarnConcreteVersionFolderChecker by registering {
        dependsOn(getByName("kotlinYarnSetup"))

        doLast {
            if (!yarn.installationDir.resolve("yarn-v1.9.3").exists()) {
                throw GradleException()
            }
        }
    }
}

kotlin.sourceSets {
    getByName("jsMain") {
        dependencies {
            implementation(kotlin("stdlib-js"))
        }
    }
}