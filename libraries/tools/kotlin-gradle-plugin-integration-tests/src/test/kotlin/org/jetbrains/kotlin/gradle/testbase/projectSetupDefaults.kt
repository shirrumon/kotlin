/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.testbase

import org.gradle.api.initialization.resolve.RepositoriesMode
import org.intellij.lang.annotations.Language
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Language("Groovy")
internal val DEFAULT_GROOVY_SETTINGS_FILE =
    """
    pluginManagement {
        repositories {
            mavenLocal()
            mavenCentral()
            google()
            gradlePluginPortal()
        }

        plugins {
            id "org.jetbrains.kotlin.jvm" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.kapt" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.android" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.js" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.native.cocoapods" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.multiplatform" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.allopen" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.spring" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.jpa" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.noarg" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.lombok" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.sam.with.receiver" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.serialization" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.assignment" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.test.fixes.android" version "${'$'}test_fixes_version"
            id "org.jetbrains.kotlin.gradle-subplugin-example" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.plugin.atomicfu" version "${'$'}kotlin_version"
            id "org.jetbrains.kotlin.test.gradle-warnings-detector" version "${'$'}test_fixes_version"
            id "org.jetbrains.kotlin.test.kotlin-compiler-args-properties" version "${'$'}test_fixes_version"
        }
        
        resolutionStrategy {
            eachPlugin {
                switch (requested.id.id) {
                    case "com.android.application":
                    case "com.android.library":
                    case "com.android.test":
                    case "com.android.dynamic-feature":
                    case "com.android.asset-pack":
                    case "com.android.asset-pack-bundle":
                    case "com.android.lint":
                    case "com.android.instantapp":
                    case "com.android.feature":
                        useModule("com.android.tools.build:gradle:${'$'}android_tools_version")
                        break
                }
            }
        }
    }

    plugins {
        id("org.jetbrains.kotlin.test.gradle-warnings-detector")
    }
    """.trimIndent()


internal fun getGroovyDependencyManagementBlock(
    gradleRepositoriesMode: RepositoriesMode,
    additionalDependencyRepositories: List<String>,
    localRepo: Path? = null,
): String =
    //language=Groovy
    """    
    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            mavenCentral()
            google()
            ivy {
                url = "https://download.jetbrains.com/kotlin/native/builds/dev"
                patternLayout {
                    artifact("[revision]/[classifier]/[artifact]-[classifier]-[revision].[ext]")
                }
                metadataSources {
                    artifact()
                }
            }
            ${additionalDependencyRepositories.map { repo -> "maven{ url = \"$repo\" }" }.joinToString("\n")}
            ${localRepo?.absolutePathString()?.let { repo -> "maven{ url = \"$repo\" }" } ?: ""}
        }
        repositoriesMode.set(${mapRepositoryModeToString(gradleRepositoriesMode)})
    }
    """.trimIndent()

@Language("kts")
internal val DEFAULT_KOTLIN_SETTINGS_FILE =
    """
    pluginManagement {
        repositories {
            mavenLocal()
            mavenCentral()
            google()
            gradlePluginPortal()
        }

        val kotlin_version: String by settings
        val android_tools_version: String by settings
        val test_fixes_version: String by settings
        plugins {
            id("org.jetbrains.kotlin.jvm") version kotlin_version
            id("org.jetbrains.kotlin.kapt") version kotlin_version
            id("org.jetbrains.kotlin.android") version kotlin_version
            id("org.jetbrains.kotlin.js") version kotlin_version
            id("org.jetbrains.kotlin.native.cocoapods") version kotlin_version
            id("org.jetbrains.kotlin.multiplatform") version kotlin_version
            id("org.jetbrains.kotlin.plugin.allopen") version kotlin_version
            id("org.jetbrains.kotlin.plugin.spring") version kotlin_version
            id("org.jetbrains.kotlin.plugin.jpa") version kotlin_version
            id("org.jetbrains.kotlin.plugin.noarg") version kotlin_version
            id("org.jetbrains.kotlin.plugin.lombok") version kotlin_version
            id("org.jetbrains.kotlin.plugin.sam.with.receiver") version kotlin_version
            id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
            id("org.jetbrains.kotlin.plugin.assignment") version kotlin_version
            id("org.jetbrains.kotlin.test.fixes.android") version test_fixes_version
            id("org.jetbrains.kotlin.gradle-subplugin-example") version kotlin_version
            id("org.jetbrains.kotlin.plugin.atomicfu") version kotlin_version
            id("org.jetbrains.kotlin.test.gradle-warnings-detector") version test_fixes_version
            id("org.jetbrains.kotlin.test.kotlin-compiler-args-properties") version test_fixes_version
        }
        
        resolutionStrategy {
            eachPlugin {
                when (requested.id.id) {
                    "com.android.application",
                    "com.android.library",
                    "com.android.test",
                    "com.android.dynamic-feature",
                    "com.android.asset-pack",
                    "com.android.asset-pack-bundle",
                    "com.android.lint",
                    "com.android.instantapp",
                    "com.android.feature",
                    "com.android.kotlin.multiplatform.library"
                       -> useModule("com.android.tools.build:gradle:${'$'}android_tools_version")
                }
            }
        }
    }

    plugins {
        id("org.jetbrains.kotlin.test.gradle-warnings-detector")
    }
    """.trimIndent()

internal fun getKotlinDependencyManagementBlock(
    gradleRepositoriesMode: RepositoriesMode,
    additionalDependencyRepositories: List<String>,
    localRepo: Path? = null,
): String =
    //language=kotlin
    """    
    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            mavenCentral()
            google()
            ivy {
                url = uri("https://download.jetbrains.com/kotlin/native/builds/dev")
                patternLayout {
                    artifact("[revision]/[classifier]/[artifact]-[classifier]-[revision].[ext]")
                }
                metadataSources {
                    artifact()
                }
            }
            ${additionalDependencyRepositories.map { repo -> "maven{ url = uri(\"$repo\") }" }.joinToString("\n")}
            ${localRepo?.absolutePathString()?.let { repo -> "maven{ url = uri(\"$repo\") }" } ?: ""}
        }
        repositoriesMode.set(${mapRepositoryModeToString(gradleRepositoriesMode)})
    }
    """.trimIndent()


private fun mapRepositoryModeToString(gradleRepositoriesMode: RepositoriesMode): String {
    return when (gradleRepositoriesMode) {
        RepositoriesMode.PREFER_PROJECT -> "RepositoriesMode.PREFER_PROJECT"
        RepositoriesMode.PREFER_SETTINGS -> "RepositoriesMode.PREFER_SETTINGS"
        RepositoriesMode.FAIL_ON_PROJECT_REPOS -> "RepositoriesMode.FAIL_ON_PROJECT_REPOS"
    }
}