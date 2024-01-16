/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.test.blackbox.support.runner

import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.konan.test.blackbox.support.TestName
import org.jetbrains.kotlin.konan.test.blackbox.support.runner.SharedExecution.sharedRunnerFor
import org.jetbrains.kotlin.konan.test.blackbox.support.settings.*
import org.jetbrains.kotlin.native.executors.*
import org.jetbrains.kotlin.test.services.JUnit5Assertions.fail
import java.util.concurrent.ConcurrentHashMap

internal object TestRunners {
    fun createProperTestRunner(testRun: TestRun, settings: Settings): Runner<Unit> =
        if (settings.get<ForcedNoopTestRunner>().value) {
            NoopTestRunner
        } else with(settings.get<KotlinNativeTargets>()) {
            val configurables = settings.configurables

            if (settings.get<XCTestRunner>().isEnabled) {
                // Forcibly run tests with XCTest
                check(configurables is AppleConfigurables) {
                    "Running tests with XCTest is not supported on non-Apple $configurables"
                }

                if (testTarget == KonanTarget.IOS_ARM64) {
                    cached { FirebaseCloudXCTestExecutor(configurables) }.sharedRunnerFor(testRun)
                } else {
                    val executor = when (testTarget) {
                        hostTarget -> cached { XCTestHostExecutor(configurables) }
                        is KonanTarget.IOS_X64, KonanTarget.IOS_SIMULATOR_ARM64 -> cached { XCTestSimulatorExecutor(configurables) }
                        else -> runningOnUnsupportedTarget("Target is not supported running with XCTest")
                    }
                    executor.toRunner(testRun)
                }
            } else if (testTarget == hostTarget) {
                LocalTestRunner(testRun)
            } else {
                val executor = when {
                    configurables is ConfigurablesWithEmulator -> cached { EmulatorExecutor(configurables) }
                    configurables is AppleConfigurables && configurables.targetTriple.isSimulator ->
                        cached { XcodeSimulatorExecutor(configurables) }
                    configurables is AppleConfigurables && RosettaExecutor.availableFor(configurables) ->
                        cached { RosettaExecutor(configurables) }
                    else -> runningOnUnsupportedTarget()
                }

                executor.toRunner(testRun)
            }
        }

    private val runnersCache: ConcurrentHashMap<String, Executor> = ConcurrentHashMap()

    private inline fun <reified T : Executor> cached(crossinline executor: () -> T): Executor =
        runnersCache.computeIfAbsent(T::class.java.simpleName) { executor() }

    // Currently, only local test name extractor is supported.
    fun extractTestNames(executable: TestExecutable, settings: Settings): Collection<TestName> = with(settings) {
        with(get<KotlinNativeTargets>()) {
            if (testTarget == hostTarget)
                LocalTestNameExtractor(executable, TestRunChecks.Default(get<Timeouts>().executionTimeout)).run()
            else
                runningOnUnsupportedTarget()
        }
    }

    private fun KotlinNativeTargets.runningOnUnsupportedTarget(message: String = ""): Nothing = fail {
        "Running tests for $testTarget on $hostTarget is not supported yet. $message"
    }
}
