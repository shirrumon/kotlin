/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.process.ExecSpec
import java.time.Duration

/**
 * Options for executing with `Executors`.
 */
interface ExecutorExecSpec : ExecSpec {
    /**
     * Target for which to execute.
     */
    var targetName: String

    /**
     * Target for which to execute.
     */
    fun targetName(targetName: String): ExecutorExecSpec {
        this.targetName = targetName
        return this
    }

    /**
     * Maximum allowed duration for execution.
     */
    var timeout: Duration

    /**
     * Maximum allowed duration for execution.
     */
    fun timeout(timeout: Duration): ExecutorExecSpec {
        this.timeout = timeout
        return this
    }

    /**
     * If not null, no execution will be performed and the string will be reported to logs as the reason.
     */
    var dryRun: String?

    /**
     * No execution will be performed, with `reason` be reported in logs.
     */
    fun dryRun(reason: String): ExecutorExecSpec {
        this.dryRun = reason
        return this
    }

    /**
     * Device id to execute on.
     */
    var deviceId: String?

    /**
     * Device id to execute on.
     */
    fun deviceId(deviceId: String?): ExecutorExecSpec {
        this.deviceId = deviceId
        return this
    }

    /**
     * Returns view of [environment] that was modified.
     */
    val additionalEnvironment: Map<String, String>
}