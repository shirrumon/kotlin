/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("ExecutorsCLI")

package org.jetbrains.kotlin.native.executors.cli

import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.native.executors.ExecuteRequest
import org.jetbrains.kotlin.native.executors.HostExecutor
import org.jetbrains.kotlin.native.executors.RosettaExecutor
import org.jetbrains.kotlin.native.executors.XcodeSimulatorExecutor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private data class Arguments(
    val dist: String,
    val dataDir: String,
    val target: String,
    val noop: Boolean,
    val timeout: String?,
    val executable: String,
    val args: List<String>,
) {
    companion object {
        fun parse(args: Array<String>): Arguments {
            var dist: String? = null
            var dataDir: String? = null
            var target: String? = null
            var noop = false
            var timeout: String? = null
            for (index in args.indices) {
                val arg = args[index]
                when {
                    arg.startsWith("--dist=") -> {
                        check(dist == null) {
                            "at arg $index: --dist was already specified: $dist"
                        }
                        dist = arg.removePrefix("--dist=")
                    }
                    arg.startsWith("--data-dir=") -> {
                        check(dataDir == null) {
                            "at arg $index: --data-dir was already specified: $dataDir"
                        }
                        dataDir = arg.removePrefix("--data-dir=")
                    }
                    arg.startsWith("--target=") -> {
                        check(target == null) {
                            "at arg $index: --target was already specified: $target"
                        }
                        target = arg.removePrefix("--target=")
                    }
                    arg.startsWith("--timeout-ms=") -> {
                        check(timeout == null) {
                            "at arg $index: --timeout-ms was already specified: $timeout"
                        }
                        timeout = arg.removePrefix("--timeout-ms=")
                    }
                    arg == "--noop" -> {
                        check(!noop) {
                            "at arg $index: --noop was already specified"
                        }
                        noop = true
                    }
                    arg == "--" -> {
                        check(dist != null) {
                            "at arg $index: --dist is unspecified"
                        }
                        check(dataDir != null) {
                            "at arg $index: --data-dir is unspecified"
                        }
                        check(target != null) {
                            "at arg $index: --target is unspecified"
                        }
                        check(index + 1 < args.size) {
                            "at arg $index: executable is unspecified"
                        }
                        val executable = args[index + 1]
                        val arguments = args.slice(index + 2 until args.size)
                        return Arguments(
                            dist = dist,
                            dataDir = dataDir,
                            target = target,
                            noop = noop,
                            timeout = timeout,
                            executable = executable,
                            args = arguments,
                        )
                    }
                    else -> {
                        error("Unknown argument: `$arg`")
                    }
                }
            }
            error("expected executable after --")
        }
    }
}

private fun execute(args: Arguments) {
    val platformManager = PlatformManager(buildDistribution(args.dist, args.dataDir))
    val target = platformManager.targetByName(args.target)
    val configurables = platformManager.platform(target).configurables
    val hostTarget = HostManager.host
    val timeout = args.timeout?.toInt()?.milliseconds ?: Duration.INFINITE

    val executor = when {
        target == hostTarget -> HostExecutor()
        configurables is AppleConfigurables && configurables.targetTriple.isSimulator -> XcodeSimulatorExecutor(configurables)
        configurables is AppleConfigurables && RosettaExecutor.availableFor(configurables) -> RosettaExecutor(configurables)
        else -> error("Cannot run for target $target")
    }

    executor.execute(ExecuteRequest(args.executable).apply {
        this.args.addAll(args.args)
        this.timeout = timeout
    })
}

fun main(args: Array<String>) {
    execute(Arguments.parse(args))
}