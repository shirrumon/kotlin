/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.mpp

import org.gradle.api.logging.LogLevel
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.testbase.*
import org.jetbrains.kotlin.test.TestMetadata
import org.junit.jupiter.api.DisplayName
import kotlin.io.path.readText
import kotlin.io.path.writeText

@MppGradlePluginTests
@DisplayName("Tests for IC with compatible overloads in common and platform sourceSets")
class CommonCodeWithPlatformSymbolsIT : KGPBaseTest() {
    override val defaultBuildOptions: BuildOptions
        get() = super.defaultBuildOptions.copy(logLevel = LogLevel.DEBUG, languageVersion = "2.0")

    @GradleTest
    @DisplayName("kt-62686-mpp-jvm-expect-new-dep")
    @TestMetadata("kt-62686-mpp-jvm-expect-dep")
    fun testKt62686v1(gradleVersion: GradleVersion) {
        project(
            "kt-62686-mpp-jvm-expect-dep", gradleVersion
        ) {
            val taskToExecute = ":compileKotlinJvm"
            build(taskToExecute) {
                assertTasksExecuted(taskToExecute)
            }

            val sourceFileToTouch = projectPath.resolve("src/commonMain/kotlin/commonTest.kt")
            val originalText = sourceFileToTouch.readText()
            val modifiedText = originalText.replace("\"!\" //", "")
            assert(originalText != modifiedText)
            sourceFileToTouch.writeText(modifiedText)

            build(taskToExecute) {
                assertTasksExecuted(taskToExecute)
       //         assertNoIncrementalBuildForAnyReason()
            }
        }
    }

    @GradleTest
    @DisplayName("kt-62686-mpp-jvm-expect-touched-dep")
    @TestMetadata("kt-62686-mpp-jvm-expect-dep")
    fun testKt62686v2(gradleVersion: GradleVersion) {
        project(
            "kt-62686-mpp-jvm-expect-dep", gradleVersion
        ) {
            val taskToExecute = ":compileKotlinJvm"
            val sourceFileToTouch = projectPath.resolve("src/commonMain/kotlin/commonTest.kt")
            val originalText = sourceFileToTouch.readText()
            val modifiedTextV2 = originalText.replace("\"!\" //", "")
            val modifiedTextV1 = modifiedTextV2.replace("Child", " Child")

            assert(originalText != modifiedTextV2)
            assert(originalText != modifiedTextV1)
            assert(modifiedTextV1 != modifiedTextV2)

            sourceFileToTouch.writeText(modifiedTextV1)

            build(taskToExecute) {
                assertTasksExecuted(taskToExecute)
            }

            sourceFileToTouch.writeText(modifiedTextV2)

            build(taskToExecute) {
                assertTasksExecuted(taskToExecute)
      ///          assertNoIncrementalBuildForAnyReason()
            }
        }
    }

    @GradleTest
    @DisplayName("kt-62686-mpp-jvm-fun-new-dep")
    @TestMetadata("kt-62686-mpp-jvm-expect-dep")
    fun testKt62686v3(gradleVersion: GradleVersion) {
        project(
            "kt-62686-mpp-jvm-expect-dep", gradleVersion
        ) {
            val taskToExecute = ":compileKotlinJvm"
            build(taskToExecute) {
                assertTasksExecuted(taskToExecute)
            }

            val sourceFileToTouch = projectPath.resolve("src/commonMain/kotlin/commonTest.kt")
            val originalText = sourceFileToTouch.readText()
            val modifiedText = originalText.replace("\"?\" //", "")
            assert(originalText != modifiedText)
            sourceFileToTouch.writeText(modifiedText)

            build(taskToExecute) {
                assertTasksExecuted(taskToExecute)
                printBuildOutput()
            }
        }
    }

    //TODO: test js implementation
    //TODO(won't do yet) test Native version of the bug - does it exist? it shouldn't, right?
    //TODO add test that confirms that the right overload is used after incremental change
}