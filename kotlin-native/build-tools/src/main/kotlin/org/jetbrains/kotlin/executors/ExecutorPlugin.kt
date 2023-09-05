/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.konan.target.PlatformManager

class ExecutorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val platformManager = project.extensions.getByType<PlatformManager>()
        // NOTE: If this persists in a gradle daemon, environment update (e.g. an Xcode update) may lead to execution failures.
        project.extensions.create<Executor>("executor", platformManager)
    }
}