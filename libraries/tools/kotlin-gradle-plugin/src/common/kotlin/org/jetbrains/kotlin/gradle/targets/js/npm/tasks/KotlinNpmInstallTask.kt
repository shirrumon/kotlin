/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.npm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.work.NormalizeLineEndings
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNodeJsExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin.Companion.kotlinNpmResolutionManager
import org.jetbrains.kotlin.gradle.targets.js.npm.KotlinNpmResolutionManager
import org.jetbrains.kotlin.gradle.targets.js.npm.asNpmEnvironment
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import org.jetbrains.kotlin.gradle.utils.unavailableValueError
import java.io.File

open class KotlinNpmInstallTask : DefaultTask() {
    init {
        check(project == project.rootProject)

        onlyIf {
            preparedFiles.all {
                it.exists()
            }
        }
    }

    @Transient
    private val nodeJs: NodeJsRootExtension? = project.rootProject.kotlinNodeJsExtension
    private val resolutionManager = project.rootProject.kotlinNpmResolutionManager.get()
    @Transient
    private val yarn = project.rootProject.yarn

    @get:Internal
    val npmEnvironment by lazy {
        nodeJs!!.asNpmEnvironment
    }

    @get:Internal
    val yarnEnv by lazy {
        yarn.requireConfigured()
    }

    @Input
    val args: MutableList<String> = mutableListOf()

    @get:Internal
    val nodeModulesDir: File by lazy {
        (nodeJs ?: unavailableValueError("nodeJs"))
            .rootPackageDir
            .resolve("node_modules")
    }

    init {
        outputs.upToDateWhen {
            nodeModulesDir.isDirectory
        }
    }

    @Suppress("unused")
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    @get:IgnoreEmptyDirectories
    @get:NormalizeLineEndings
    @get:InputFiles
    val packageJsonFiles: Collection<File> by lazy {
        resolutionManager.packageJsonFiles
    }

    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    @get:IgnoreEmptyDirectories
    @get:NormalizeLineEndings
    @get:InputFiles
    val preparedFiles: Collection<File> by lazy {
        (nodeJs ?: unavailableValueError("nodeJs")).packageManager.preparedFiles(nodeJs.asNpmEnvironment)
    }

    @get:OutputFile
    val yarnLock: File by lazy {
        (nodeJs ?: unavailableValueError("nodeJs")).rootPackageDir.resolve("yarn.lock")
    }

    @TaskAction
    fun resolve() {
        resolutionManager.installIfNeeded(
            args = args,
            services = services,
            logger = logger,
            npmEnvironment,
            yarnEnv
        ) ?: throw (resolutionManager.state as KotlinNpmResolutionManager.ResolutionState.Error).wrappedException
    }

    companion object {
        const val NAME = "kotlinNpmInstall"
    }
}