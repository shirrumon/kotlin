/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.internal.DefaultExecSpec
import org.gradle.process.internal.ExecException
import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.native.executors.*
import java.io.Serializable
import java.time.Duration as JavaDuration
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

open class Executor private constructor(private val serialized: Serialized) : Serializable {
    private val platformManager by serialized::platformManager

    var defaultTarget: KonanTarget
        get() = platformManager.targetByName(serialized.defaultTargetName)
        set(value) {
            serialized.defaultTargetName = value.name
        }

    var defaultDryRun by serialized::defaultDryRun

    var defaultTimeout: Duration
        get() = serialized.defaultTimeout.toKotlinDuration()
        set(value) {
            serialized.defaultTimeout = value.toJavaDuration()
        }

    var defaultJavaTimeout: JavaDuration by serialized::defaultTimeout

    var defaultDeviceId by serialized::defaultDeviceId

    @Inject
    constructor(platformManager: PlatformManager) : this(Serialized(
            platformManager = platformManager,
            defaultTargetName = platformManager.hostPlatform.target.name,
            defaultDryRun = null,
            defaultTimeout = Duration.INFINITE.toJavaDuration(),
            defaultDeviceId = null,
    ))

    fun exec(objectFactory: ObjectFactory, action: Action<in ExecutorSpec>): ExecResult {
        val execSpec = object : ExecSpec by objectFactory.newInstance<DefaultExecSpec>(), ExecutorSpec {
            override var target: KonanTarget = defaultTarget
            override var dryRun: String? = defaultDryRun
            override var timeout: Duration = defaultTimeout
            override var deviceId: String? = defaultDeviceId
        }.apply {
            action.execute(this)
        }

        val target = execSpec.target
        val configurables = platformManager.platform(target).configurables

        val impl = when {
            execSpec.dryRun != null -> NoOpExecutor(explanation = execSpec.dryRun)
            target == HostManager.host -> HostExecutor()
            configurables is ConfigurablesWithEmulator && target != HostManager.host -> EmulatorExecutor(configurables)
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
                timeout = execSpec.timeout,
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
            environment.putAll(execSpec.environment.mapValues { it.toString() })
        }
        val response = impl.execute(request)
        return object : ExecResult {
            override fun getExitValue() = response.exitCode ?: -1

            override fun assertNormalExitValue(): ExecResult {
                if (response.exitCode == null) {
                    throw TimeoutException(execSpec.timeout)
                }
                if (exitValue != 0) {
                    throw ExecException("Failed with exit code $exitValue")
                }
                return this
            }

            override fun rethrowFailure(): ExecResult {
                return this
            }
        }
    }

    private fun writeReplace(): Any = serialized

    private data class Serialized(
            val platformManager: PlatformManager,
            var defaultTargetName: String,
            var defaultDryRun: String?,
            var defaultTimeout: JavaDuration,
            var defaultDeviceId: String?,
    ) : Serializable {
        companion object {
            private const val serialVersionUID: Long = 0L
        }

        private fun readResolve(): Any = Executor(this)
    }
}