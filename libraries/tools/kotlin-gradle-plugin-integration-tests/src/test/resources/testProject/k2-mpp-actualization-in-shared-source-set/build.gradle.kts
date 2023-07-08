import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvm("intermediate")
    //jvm("myJvm")
    //linuxX64()

    val commonMain by sourceSets.getting

    val jvmMain by sourceSets.creating {
        dependsOn(commonMain)
    }

    val intermediateMain by sourceSets.getting {
        dependsOn(jvmMain)
    }

    /*val linuxX64Main by sourceSets.getting {
        dependsOn(intermediateMain)
    }*/

    sourceSets.configureEach {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}
