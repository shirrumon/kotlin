/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.executors

import org.jetbrains.kotlin.konan.target.PlatformManager
import java.io.Serializable
import java.time.Duration

/**
 * Default executor options.
 *
 * Created by [ExecutorsPlugin] and is installed as an extension.
 *
 * @see ExecutorsPlugin
 */
// TODO: It's better to represent the properties here via gradle `Provider`. However,
//       providers created from `ObjectFactory` are not `Serializable` (and we need
//       them to be serializable to pass `this` into gradle workers). And implementing
//       custom instances of `Provider` requires a ton of code. So, let's just do this.
class Executors(
        val platformManager: PlatformManager,
) : Serializable {
    /**
     * Target for which to execute.
     */
    var defaultTargetName: String = platformManager.hostPlatform.target.name

    /**
     * Maximum allowed duration for execution.
     */
    var defaultTimeout: Duration = Duration.ofMillis(Long.MAX_VALUE)

    /**
     * If not null, no execution will be performed and the string will be reported to logs as the reason.
     */
    var defaultDryRun: String? = null

    /**
     * Device id to execute on.
     */
    var defaultDeviceId: String? = null
}