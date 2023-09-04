/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.process.ExecSpec
import org.jetbrains.kotlin.konan.target.KonanTarget
import kotlin.time.Duration

interface ExecutorSpec : ExecSpec {
    var target: KonanTarget
    fun setTarget(target: KonanTarget): ExecutorSpec {
        this.target = target
        return this
    }

    var dryRun: String?
    fun setDryRun(dryRun: String?): ExecutorSpec {
        this.dryRun = dryRun
        return this
    }

    var timeout: Duration
    fun setTimeout(timeout: Duration): ExecutorSpec {
        this.timeout = timeout
        return this
    }

    var deviceId: String?
    fun setDeviceId(deviceId: String?): ExecutorSpec {
        this.deviceId = deviceId
        return this
    }
}