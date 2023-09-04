/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.gradle.process.internal.ExecException
import kotlin.time.Duration

class TimeoutException(
        val timeout: Duration,
) : ExecException("Execution exceeded time limit $timeout") {
}