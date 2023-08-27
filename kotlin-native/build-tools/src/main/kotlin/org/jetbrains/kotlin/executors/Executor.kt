/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.JavaExecSpec
import org.gradle.process.internal.DefaultExecSpec
import java.io.Serializable

class Executor private constructor(private val serialized: Serialized) : Serializable {
    internal constructor(distributionDir: String, dataDir: String, classpath: Array<String>) : this(Serialized(
            distributionDir, dataDir, classpath
    ))

    internal val distributionDir by serialized::distributionDir
    internal val dataDir by serialized::dataDir
    internal val classpath by serialized::classpath

    private fun writeReplace(): Any = serialized

    private data class Serialized(
            val distributionDir: String,
            val dataDir: String,
            val classpath: Array<String>,
    ) : Serializable {
        companion object {
            private const val serialVersionUID: Long = 0L
        }

        private fun readResolve(): Any = Executor(this)
    }
}

private fun Executor.asJavaExecSpecAction(objects: ObjectFactory, action: Action<in ExecutorSpec>): Action<in JavaExecSpec> = Action {
    val spec = DefaultExecutorSpec(objects.newInstance<DefaultExecSpec>()).apply {
        action.execute(this)
    }
    classpath(*this@asJavaExecSpecAction.classpath)
    mainClass.set("org.jetbrains.kotlin.native.executors.cli.ExecutorsCLI")
    args("--dist=${this@asJavaExecSpecAction.distributionDir}")
    args("--data-dir=${this@asJavaExecSpecAction.dataDir}")
    args("--target=${spec.target.name}")
    args("--timeout-ms=${spec.timeout.toMillis()}")
    if (spec.dryRun) {
        args("--noop")
    }
    spec.deviceId?.let {
        args("--device-id=$it")
    }
    args("--")
    args(spec.executable)
    args!!.addAll(spec.args)
    spec.environment?.let {
        environment = it
    }
    spec.workingDir?.let {
        workingDir = it
    }
    isIgnoreExitValue = spec.isIgnoreExitValue
    spec.standardInput?.let {
        standardInput = it
    }
    spec.standardOutput?.let {
        standardOutput = it
    }
    spec.errorOutput?.let {
        errorOutput = it
    }
}

// TODO: Handle timeout. Return -1 as an exit code (or throw an exception?)

fun ExecOperations.executorExec(objects: ObjectFactory, executor: Executor, action: Action<in ExecutorSpec>): ExecResult =
        javaexec(executor.asJavaExecSpecAction(objects, action))

fun Project.executorExec(executor: Executor, action: Action<in ExecutorSpec>): ExecResult =
        javaexec(executor.asJavaExecSpecAction(objects, action))