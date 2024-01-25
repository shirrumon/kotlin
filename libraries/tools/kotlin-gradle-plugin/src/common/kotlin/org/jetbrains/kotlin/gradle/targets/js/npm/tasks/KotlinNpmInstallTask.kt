/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.npm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.gradle.work.NormalizeLineEndings
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNodeJsExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.KotlinNpmResolutionManager
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject
import org.jetbrains.kotlin.gradle.targets.js.npm.UsesKotlinNpmResolutionManager
import org.jetbrains.kotlin.gradle.targets.js.npm.asNpmEnvironment
import org.jetbrains.kotlin.gradle.targets.js.npm.asYarnEnvironment
import org.jetbrains.kotlin.gradle.targets.js.npm.resolver.KotlinRootNpmResolver
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import org.jetbrains.kotlin.gradle.utils.getFile
import java.io.File

@DisableCachingByDefault
abstract class KotlinNpmInstallTask :
    DefaultTask(),
    UsesKotlinNpmResolutionManager {
    init {
        notCompatibleWithConfigurationCache("KotlinNpmInstallTask uses Task.project")
        check(project == project.rootProject)
    }

    // Only in configuration phase
    // Not part of configuration caching

    private val nodeJs: NodeJsRootExtension
        get() = project.rootProject.kotlinNodeJsExtension

    private val yarn
        get() = project.rootProject.yarn

    private val rootResolver: KotlinRootNpmResolver
        get() = nodeJs.resolver

    private val packagesDir: Provider<Directory>
        get() = nodeJs.projectPackagesDirectory

    // -----

    private val npmEnvironment by lazy {
        nodeJs.requireConfigured().asNpmEnvironment
    }

    private val yarnEnv by lazy {
        yarn.requireConfigured().asYarnEnvironment
    }

    @Input
    val args: MutableList<String> = mutableListOf()

    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    @get:IgnoreEmptyDirectories
    @get:NormalizeLineEndings
    @get:InputFiles
    val preparedFiles: Collection<File> by lazy {
        nodeJs.packageManager.preparedFiles(npmEnvironment)
    }

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:IgnoreEmptyDirectories
    @get:NormalizeLineEndings
    @get:InputFiles
    val packageJsonFiles: FileCollection = project.objects.fileCollection().from(
        {
            rootResolver.projectResolvers.values
                .flatMap { it.compilationResolvers }
                .map { it.compilationNpmResolution }
                .map { resolution ->
                    val name = resolution.npmProjectName
                    packagesDir.map { it.dir(name).file(NpmProject.PACKAGE_JSON) }
                }
        }
    )

    @get:OutputFile
    val yarnLockFile: Provider<RegularFile> = nodeJs.rootPackageDirectory.map { it.file("yarn.lock") }

    @Deprecated(
        "This property is deprecated and will be removed in future. Use yarnLockFile instead",
        replaceWith = ReplaceWith("yarnLockFile")
    )
    @get:Internal
    val yarnLock: File
        get() = yarnLockFile.getFile()

    // node_modules as OutputDirectory is performance problematic
    // so input will only be existence of its directory
    @get:Internal
    val nodeModules: Provider<Directory> = nodeJs.rootPackageDirectory.map { it.dir("node_modules") }

    @TaskAction
    fun resolve() {
        npmResolutionManager.get()
            .installIfNeeded(
                args = args,
                services = services,
                logger = logger,
                npmEnvironment,
                yarnEnv
            ) ?: throw (npmResolutionManager.get().state as KotlinNpmResolutionManager.ResolutionState.Error).wrappedException
    }

    companion object {
        const val NAME = "kotlinNpmInstall"
    }
}