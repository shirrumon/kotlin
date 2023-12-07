/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.native.toolchain

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.compilerRunner.konanDataDir
import org.jetbrains.kotlin.compilerRunner.konanHome
import org.jetbrains.kotlin.compilerRunner.kotlinNativeToolchainEnabled
import org.jetbrains.kotlin.gradle.plugin.KOTLIN_NATIVE_COMPILER_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PropertiesProvider.Companion.kotlinPropertiesProvider
import org.jetbrains.kotlin.gradle.targets.native.internal.NativeDistributionTypeProvider
import org.jetbrains.kotlin.gradle.targets.native.internal.PlatformLibrariesGenerator
import org.jetbrains.kotlin.gradle.utils.NativeCompilerDownloader
import org.jetbrains.kotlin.gradle.utils.filesProvider
import org.jetbrains.kotlin.gradle.utils.getFile
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.DependencyDirectories
import java.io.File

/**
 * This is a nested provider for all native tasks
 */
internal class KotlinNativeProvider(project: Project, konanTarget: KonanTarget) {

    @get:Internal
    val konanDataDir: Provider<String?> = project.provider { project.konanDataDir }

    @get:Internal
    val konanHome: Provider<Directory> = project.layout.dir(
        project.provider {
            project.konanHome
        }
    )

    @get:Internal
    internal val kotlinNativeCompilerConfiguration: ConfigurableFileCollection = project.filesProvider {
        project.configurations.named(
            KOTLIN_NATIVE_COMPILER_CONFIGURATION_NAME
        )
    }

    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    @get:InputDirectory
    internal val kotlinNativeCompilerDirectory: Provider<Directory> = konanHome.map {
        if (project.kotlinNativeToolchainEnabled
            && (project.kotlinPropertiesProvider.nativeReinstall || !konanHome.getFile().exists())
        ) {
            val kotlinNativeExtractedFolder =
                kotlinNativeCompilerConfiguration.singleOrNull() ?: error("Kotlin Native dependency has not been properly resolved.")
            val kotlinNativeFolderName = NativeCompilerDownloader.getDependencyNameWithOsAndVersion(project)
            project.prepareKotlinNativeCompiler(kotlinNativeExtractedFolder.resolve(kotlinNativeFolderName))
        }
        it
    }

    @get:Input
    internal val nativeCompilerDependencies: Provider<String> = project.provider {
        if (project.kotlinNativeToolchainEnabled) {
            if (!kotlinNativeCompilerDirectory.getFile().exists()) {
                throw IllegalStateException("There is no downloaded kotlin native with path ${kotlinNativeCompilerDirectory.get()}")
            }
            setupKotlinNativeDependencies(project, konanTarget)
        }
        DependencyDirectories.getDependenciesRoot(konanDataDir.get()).absolutePath
    }


    private fun Project.prepareKotlinNativeCompiler(gradleCachesKotlinNativeDir: File) {

        if (project.kotlinPropertiesProvider.nativeReinstall) {
            NativeCompilerDownloader.getCompilerDirectory(project).deleteRecursively()
        }

        if (!konanHome.exists()) {
            logger.info("Moving Kotlin/Native compiler from tmp directory $gradleCachesKotlinNativeDir to ${konanHome.absolutePath}")
            copy {
                it.from(gradleCachesKotlinNativeDir)
                it.into(konanHome)
            }
            logger.info("Moved Kotlin/Native compiler from $gradleCachesKotlinNativeDir to ${konanHome.absolutePath}")
        }
    }

    private fun setupKotlinNativeDependencies(project: Project, konanTarget: KonanTarget) {
        val distributionType = NativeDistributionTypeProvider(project).getDistributionType()
        if (distributionType.mustGeneratePlatformLibs) {
            PlatformLibrariesGenerator(project, konanTarget).generatePlatformLibsIfNeeded()
        }
    }

}