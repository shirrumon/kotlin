/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import org.gradle.process.JavaExecSpec

/**
 * Like [Project.exec] but execute with [Executors] configured by [executors].
 */
fun Project.executorExec(executors: Executors, action: Action<in ExecutorExecSpec>) = object : ExecOperations {
    override fun exec(action: Action<in ExecSpec>) = project.exec(action)
    override fun javaexec(action: Action<in JavaExecSpec>) = project.javaexec(action)
}.executorExec(executors, action)