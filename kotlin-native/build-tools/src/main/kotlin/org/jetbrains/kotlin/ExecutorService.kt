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

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.*
import org.jetbrains.kotlin.executors.Executors
import org.jetbrains.kotlin.executors.executorExec

import java.io.*

import java.nio.file.Path

/**
 * A replacement of the standard `exec {}`
 * @see org.gradle.api.Project.exec
 */
interface ExecutorService {
    val project: Project
    fun execute(closure: Closure<in ExecSpec>): ExecResult? = execute { project.configure(this, closure) }
    fun execute(action: Action<in ExecSpec>): ExecResult?
}

/**
 * Creates an ExecutorService depending on a test target -Ptest_target
 */
fun create(project: Project): ExecutorService {
    return object : ExecutorService {
        override val project: Project
            get() = project

        override fun execute(action: Action<in ExecSpec>) = project.executorExec(project.extensions.getByType<Executors>(), action)
    }
}

data class ProcessOutput(var stdOut: String, var stdErr: String, var exitCode: Int)

/**
 * Runs process using a given executor.
 *
 * @param executor a method that is able to run a given executable, e.g. ExecutorService::execute
 * @param executable a process executable to be run
 * @param args arguments for a process
 */
fun runProcess(executor: (Action<in ExecSpec>) -> ExecResult?,
               executable: String, args: List<String>): ProcessOutput {
    val outStream = ByteArrayOutputStream()
    val errStream = ByteArrayOutputStream()

    val execResult = executor(Action {
        this.executable = executable
        this.args = args.toList()
        this.standardOutput = outStream
        this.errorOutput = errStream
        this.isIgnoreExitValue = true
    })

    checkNotNull(execResult)

    val stdOut = outStream.toString("UTF-8")
    val stdErr = errStream.toString("UTF-8")

    return ProcessOutput(stdOut, stdErr, execResult.exitValue)
}

fun runProcess(executor: (Action<in ExecSpec>) -> ExecResult?,
               executable: String, vararg args: String) = runProcess(executor, executable, args.toList())

/**
 * Runs process using a given executor.
 *
 * @param executor a method that is able to run a given executable, e.g. ExecutorService::execute
 * @param executable a process executable to be run
 * @param args arguments for a process
 * @param input an input string to be passed through the standard input stream
 */
fun runProcessWithInput(executor: (Action<in ExecSpec>) -> ExecResult?,
                        executable: String, args: List<String>, input: String): ProcessOutput {
    val outStream = ByteArrayOutputStream()
    val errStream = ByteArrayOutputStream()
    val inStream = ByteArrayInputStream(input.toByteArray())

    val execResult = executor(Action {
        this.executable = executable
        this.args = args.toList()
        this.standardOutput = outStream
        this.errorOutput = errStream
        this.isIgnoreExitValue = true
        this.standardInput = inStream
    })

    checkNotNull(execResult)

    val stdOut = outStream.toString("UTF-8")
    val stdErr = errStream.toString("UTF-8")

    return ProcessOutput(stdOut, stdErr, execResult.exitValue)
}

/**
 * The [ExecutorService] being set in the given project.
 * @throws IllegalStateException if there are no executor in the project.
 */
val Project.executor: ExecutorService
    get() = this.extensions.findByName("executor") as? ExecutorService
            ?: throw IllegalStateException("Executor wasn't found")

/**
 * Executes the [executable] with the given [arguments]
 * and checks that the program finished with zero exit code.
 */
fun Project.executeAndCheck(executable: Path, arguments: List<String> = emptyList()) {
    val (stdOut, stdErr, exitCode) = runProcess(
            executor = executor::execute,
            executable = executable.toString(),
            args = arguments
    )

    println("""
            |stdout: $stdOut
            |stderr: $stdErr
            """.trimMargin())
    check(exitCode == 0) { "Execution failed with exit code: $exitCode" }
}

/**
 * Returns [project]'s process executor.
 * @see Project.exec
 */
fun localExecutor(project: Project) = { a: Action<in ExecSpec> -> project.exec(a) }