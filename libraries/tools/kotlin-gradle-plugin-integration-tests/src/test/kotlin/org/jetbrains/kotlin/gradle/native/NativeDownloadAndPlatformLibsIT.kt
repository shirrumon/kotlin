/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.native

import org.gradle.testkit.runner.BuildResult
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnostics
import org.jetbrains.kotlin.gradle.testbase.*
import org.jetbrains.kotlin.gradle.utils.NativeCompilerDownloader
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.presetName
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.appendText

// We temporarily disable it for windows until a proper fix is found for this issue:
// https://youtrack.jetbrains.com/issue/KT-60138/NativeDownloadAndPlatformLibsIT-fails-on-Windows-OS
@OsCondition(
    supportedOn = [OS.MAC, OS.LINUX], enabledOnCI = [OS.MAC, OS.LINUX]
)
@DisplayName("Tests for K/N builds with native downloading and platform libs")
@NativeGradlePluginTests
@Deprecated(
    message =
    """
    This is deprecated test class with regression checks for old downloading logic.
    We support it during migration to kotlin native toolchain.
    If you want to add test here, be sure that you have added similar test with `-Pkotlin.native.toolchain.enabled=true`.
    """,
    ReplaceWith("NativeDownloadAndPlatformLibsIT")
)
class NativeDownloadAndPlatformLibsIT : KGPBaseTest() {

    private val platformName: String = HostManager.platformName()
    private val currentCompilerVersion = NativeCompilerDownloader.DEFAULT_KONAN_VERSION

    override val defaultBuildOptions: BuildOptions
        get() = super.defaultBuildOptions.withBundledKotlinNative().copy(
            // Disabling toolchain feature for checking stable logic with downloading kotlin native
            freeArgs = listOf("-Pkotlin.native.toolchain.enabled=false"),
            // For each test in this class, we need to provide an isolated .konan directory,
            // so we create it within each test project folder
            konanDataDir = workingDir.resolve(".konan")
                .toFile()
                .apply { mkdirs() }.toPath(),
        )

    @OptIn(EnvironmentalVariablesOverride::class)
    @DisplayName("K/N Gradle project build (on Linux or Mac) with a dependency from a Maven")
    @GradleTest
    fun testSetupCommonOptionsForCaches(gradleVersion: GradleVersion, @TempDir tempDir: Path) {
        val anotherKonanDataDir = tempDir.resolve(".konan2")
        nativeProject(
            "native-with-maven-dependencies",
            gradleVersion = gradleVersion,
            environmentVariables = EnvironmentalVariables(Pair("KONAN_DATA_DIR", anotherKonanDataDir.absolutePathString())),
            forceOutput = true
        ) {
            build(
                "linkDebugExecutableNative",
                buildOptions = defaultBuildOptions.copy(
                    nativeOptions = defaultBuildOptions.nativeOptions.copy(
                        cacheKind = null
                    )
                )
            ) {
                assertOutputDoesNotContain("w: Failed to build cache")
                assertTasksExecuted(":linkDebugExecutableNative")
                assertDirectoryDoesNotExist(anotherKonanDataDir)
            }
        }
    }

    private fun platformLibrariesProject(
        vararg targets: String,
        gradleVersion: GradleVersion,
        test: TestProject.() -> Unit = {},
    ) {
        nativeProject("native-platform-libraries", gradleVersion) {
            buildGradleKts.appendText(
                targets.joinToString(prefix = "\n", separator = "\n") {
                    "kotlin.$it()"
                }
            )
            test()
        }
    }

    private fun TestProject.buildWithLightDist(
        vararg tasks: String,
        buildOptions: BuildOptions = defaultBuildOptions.copy(),
        assertions: BuildResult.() -> Unit,
    ) =
        build(
            *tasks,
            buildOptions = buildOptions.copy(
                nativeOptions = buildOptions.nativeOptions.copy(distributionType = "light")
            ),
            assertions = assertions
        )

}
