/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.konan.util.DependencyDirectories
import javax.inject.Inject

abstract class ExecutorsExtension @Inject constructor(private val project: Project) {
    val executorConfiguration: Configuration by project.configurations.creating {
        isCanBeConsumed = false
        isCanBeResolved = true
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
            // TODO: Why classes?
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.CLASSES))
            defaultDependencies {
                add(project.dependencies.project(":native:executors"))
            }
        }
    }

    val executor: Executor by lazy {
        Executor(
                distributionDir = project.project(":kotlin-native").projectDir.absolutePath,
                dataDir = DependencyDirectories.localKonanDir.absolutePath,
                classpath = executorConfiguration.files.map { it.absolutePath }.toTypedArray()
        )
    }
}

open class ExecutorsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create<ExecutorsExtension>("executors")
    }
}