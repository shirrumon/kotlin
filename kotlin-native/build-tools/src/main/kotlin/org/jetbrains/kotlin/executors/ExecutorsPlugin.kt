/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.konan.target.PlatformManager

/**
 * Creates an extension [Executors] named `executors`.
 *
 * @see Executors
 */
class ExecutorsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.add("executors", Executors(
                platformManager = project.extensions.getByType<PlatformManager>()
        ))
    }
}