/*
 * Copyright 2010-2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(kotlin.time.ExperimentalTime::class)
package org.jetbrains.kotlin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.*
import org.jetbrains.kotlin.executors.Executor

import java.io.*

data class ProcessOutput(var stdOut: String, var stdErr: String, var exitCode: Int)

/**
 * Runs process using a given executor.
 *
 * @param executor a method that is able to run a given executable, e.g. ExecutorService::execute
 * @param executable a process executable to be run
 * @param args arguments for a process
 * @param input an input string to be passed through the standard input stream
 */
fun runProcess(
        executor: (Action<in ExecSpec>) -> ExecResult?,
        executable: String,
        args: List<String> = emptyList(),
        input: String? = null,
): ProcessOutput {
    val outStream = ByteArrayOutputStream()
    val errStream = ByteArrayOutputStream()

    val execResult = executor(Action {
        this.executable = executable
        this.args = args
        this.standardOutput = outStream
        this.errorOutput = errStream
        this.isIgnoreExitValue = true
        input?.let {
            this.standardInput = ByteArrayInputStream(it.toByteArray())
        }
    })

    checkNotNull(execResult)

    val stdOut = outStream.toString("UTF-8")
    val stdErr = errStream.toString("UTF-8")

    return ProcessOutput(stdOut, stdErr, execResult.exitValue)
}

val Project.executor
    get() = { a: Action<in ExecSpec> -> extensions.getByType<Executor>().exec(objects, a) }

val Project.hostExecutor
    get() = { a: Action<in ExecSpec> -> project.exec(a) }