/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.process.ExecSpec
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.time.Duration

interface ExecutorSpec : ExecSpec {
    var timeout: Duration
    var target: KonanTarget
    var dryRun: Boolean
    var deviceId: String?
}