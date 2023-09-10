/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("ExecutorsCLI")

package org.jetbrains.kotlin.native.executors.cli

import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.native.executors.*
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private data class Spec(
    val dist: String,
    val dataDir: String,
    val target: String,
    val dryRun: String?,
    val timeoutMs: String?,
    val deviceId: String?,
    val additionalEnvironment: Map<String, String>,
    val executable: String,
    val args: List<String>,
) {
    companion object {
        fun parse(args: Array<String>): Spec {
            var dist: String? = null
            var dataDir: String? = null
            var target: String? = null
            var dryRun: String? = null
            var timeoutMs: String? = null
            var deviceId: String? = null
            val additionalEnvironment = mutableMapOf<String, String>()
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
                        check(timeoutMs == null) {
                            "at arg $index: --timeout-ms was already specified: $timeoutMs"
                        }
                        timeoutMs = arg.removePrefix("--timeout-ms=")
                    }
                    arg.startsWith("--dry-run=") -> {
                        check(dryRun == null) {
                            "at arg $index: --dry-run was already specified: $dryRun"
                        }
                        dryRun = arg.removePrefix("--dry-run=")
                    }
                    arg.startsWith("--device-id=") -> {
                        check(deviceId == null) {
                            "at arg $index: --device-id was already specified: $deviceId"
                        }
                        deviceId = arg.removePrefix("--device-id=")
                    }
                    arg.startsWith("--env=") -> {
                        val value = arg.removePrefix("--env=")
                        val envItem = value.split("=", limit = 2)
                        check(envItem.size >= 2) {
                            "at arg $index: --env is invalid. Must be key=value: $value"
                        }
                        additionalEnvironment[envItem[0]] = envItem[1]
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
                        return Spec(
                            dist = dist,
                            dataDir = dataDir,
                            target = target,
                            dryRun = dryRun,
                            timeoutMs = timeoutMs,
                            deviceId = deviceId,
                            additionalEnvironment = additionalEnvironment,
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

fun main(args: Array<String>) {
    val execSpec = Spec.parse(args)

    val platformManager = PlatformManager(buildDistribution(execSpec.dist, execSpec.dataDir))
    val target = platformManager.targetByName(execSpec.target)
    val configurables = platformManager.platform(target).configurables

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
        args = execSpec.args.toMutableList(),
        timeout = execSpec.timeoutMs?.toInt()?.milliseconds ?: Duration.INFINITE,
    ).apply {
        stdin = System.`in`
        stdout = System.out
        stderr = System.err
        environment.putAll(execSpec.additionalEnvironment)
    }
    val response = executor.execute(request)
    exitProcess(response.exitCode ?: -1)
}