/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Action
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult

/**
 * Like [ExecOperations.exec] but execute with [Executors] configured by [executors].
 */
fun ExecOperations.executorExec(executors: Executors, action: Action<in ExecutorExecSpec>): ExecResult {
    return javaexec {
        val baseExecSpec = this
        val execSpec = DefaultExecutorExecSpec(executors, baseExecSpec).apply {
            action.execute(this)
        }
        classpath(executors.executorsClasspath)
        mainClass.set("org.jetbrains.kotlin.native.executors.cli.ExecutorsCLI")
        args("--dist=${executors.distributionDir}")
        args("--data-dir=${executors.dataDir}")
        args("--target=${execSpec.targetName}")
        args("--timeout-ms=${execSpec.timeout.toMillis()}")
        execSpec.dryRun?.let {
            args("--dry-run=$it")
        }
        execSpec.deviceId?.let {
            args("--device-id=$it")
        }
        execSpec.additionalEnvironment.forEach { (key, value) ->
            args("--env=$key=$value")
        }
        args("--")
        args(execSpec.commandLine)
        execSpec.standardInput?.let {
            standardInput = it
        }
        execSpec.standardOutput?.let {
            standardOutput = it
        }
        execSpec.errorOutput?.let {
            errorOutput = it
        }
    }
}