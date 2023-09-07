/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.process.BaseExecSpec
import org.gradle.process.CommandLineArgumentProvider

internal class DefaultExecutorExecSpec(
        executors: Executors,
        baseExecSpec: BaseExecSpec,
) : BaseExecSpec by baseExecSpec, ExecutorExecSpec {
    override var targetName = executors.defaultTargetName
    override var timeout = executors.defaultTimeout
    override var dryRun = executors.defaultDryRun
    override var deviceId = executors.defaultDeviceId

    override val additionalEnvironment: Map<String, String>
        get() {
            val originalEnv = System.getenv()
            val modifiedEnv = this.environment.mapValues { it.value.toString() }
            return buildMap {
                modifiedEnv.forEach { (key, value) ->
                    if (key !in originalEnv || originalEnv[key] != value) {
                        put(key, value)
                    }
                }
            }
        }

    private val arguments = mutableListOf<Any>()
    private val argumentProviders = mutableListOf<CommandLineArgumentProvider>()

    private fun setCommandLineImpl(args: List<*>) {
        setExecutable(args[0])
        setArgs(args.subList(1, args.size).toMutableList())
    }

    override fun setCommandLine(args: MutableList<String>) = setCommandLineImpl(args)
    override fun setCommandLine(vararg args: Any) = setCommandLineImpl(args.toList())
    override fun setCommandLine(args: MutableIterable<*>) = setCommandLineImpl(args.toList())

    override fun commandLine(vararg args: Any): DefaultExecutorExecSpec {
        setCommandLineImpl(args.toList())
        return this
    }

    override fun commandLine(args: MutableIterable<*>): DefaultExecutorExecSpec {
        setCommandLineImpl(args.toList())
        return this
    }

    override fun getCommandLine() = mutableListOf(executable).apply {
        addAll(args)
        addAll(argumentProviders.flatMap { it.asArguments() })
    }

    override fun args(vararg args: Any): DefaultExecutorExecSpec {
        arguments.addAll(args)
        return this
    }

    override fun args(args: MutableIterable<*>): DefaultExecutorExecSpec {
        arguments.addAll(args.map { it!! })
        return this
    }

    override fun setArgs(args: MutableList<String>): DefaultExecutorExecSpec {
        arguments.clear()
        return args(args)
    }

    override fun setArgs(args: MutableIterable<*>): DefaultExecutorExecSpec {
        arguments.clear()
        return args(args)
    }

    override fun getArgs(): MutableList<String> = arguments.map { it.toString() }.toMutableList()

    override fun getArgumentProviders() = argumentProviders
}