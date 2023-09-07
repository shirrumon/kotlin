/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Action
import org.gradle.internal.file.PathToFileResolver
import org.gradle.process.BaseExecSpec
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecException
import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.native.executors.*
import java.io.File
import kotlin.time.toKotlinDuration

/**
 * Like [ExecOperations.exec] but execute with [Executors] configured by [executors].
 */
fun ExecOperations.executorExec(executors: Executors, action: Action<in ExecutorExecSpec>): ExecResult {
    // TODO: Make executors be executed via `javaexec`, and use the `JavaExecSpec` as `baseExecSpec` here.
    val baseExecSpec: BaseExecSpec = DefaultExecSpec(object : PathToFileResolver {
        override fun resolve(arg: Any): File {
            return arg as File // Works in our use cases.
        }

        override fun newResolver(p0: File): PathToFileResolver {
            TODO("Not yet implemented")
        }

        override fun canResolveRelativePath(): Boolean {
            TODO("Not yet implemented")
        }
    })
    val execSpec = DefaultExecutorExecSpec(executors, baseExecSpec).apply {
        action.execute(this)
    }

    val target = executors.platformManager.targetByName(execSpec.targetName)
    val configurables = executors.platformManager.platform(target).configurables

    val executor = execSpec.dryRun?.let { NoOpExecutor(explanation = it) } ?: when {
        target == HostManager.host -> HostExecutor()
        configurables is ConfigurablesWithEmulator -> EmulatorExecutor(configurables)
        configurables is AppleConfigurables && configurables.targetTriple.isSimulator -> XcodeSimulatorExecutor(configurables).apply {
            execSpec.deviceId?.let {
                deviceId = it
            }
        }
        configurables is AppleConfigurables && RosettaExecutor.availableFor(configurables) -> RosettaExecutor(configurables)
        else -> error("Cannot run for target $target")
    }
    val request = ExecuteRequest(
            executableAbsolutePath = execSpec.executable,
            args = execSpec.args,
            timeout = execSpec.timeout.toKotlinDuration(),
    ).apply {
        execSpec.standardInput?.let {
            stdin = it
        }
        execSpec.standardOutput?.let {
            stdout = it
        }
        execSpec.errorOutput?.let {
            stderr = it
        }
        environment.putAll(execSpec.additionalEnvironment)
    }
    val response = executor.execute(request)
    return object : ExecResult {
        override fun getExitValue() = response.exitCode ?: -1

        override fun assertNormalExitValue(): ExecResult {
            val code = response.exitCode ?: throw ExecException("Timeout running ${execSpec.executable}")
            if (code != 0) {
                throw ExecException("Failed with exit code $code")
            }
            return this
        }

        override fun rethrowFailure(): ExecResult {
            return this
        }
    }
}