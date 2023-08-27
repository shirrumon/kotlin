/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.process.ExecSpec
import org.gradle.process.internal.DefaultExecSpec
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.time.Duration

internal class DefaultExecutorSpec(private val execSpec: DefaultExecSpec) : ExecSpec by execSpec, ExecutorSpec {
    override var timeout: Duration = Duration.ofMillis(Long.MAX_VALUE)
    override var target: KonanTarget = HostManager.host
    override var dryRun: Boolean = false
    override var deviceId: String? = null
}