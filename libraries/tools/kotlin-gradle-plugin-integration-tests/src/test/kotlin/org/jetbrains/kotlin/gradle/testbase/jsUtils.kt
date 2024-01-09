/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.testbase

/**
 * Modify buildGradleKts with applying Yarn .
 *
 */
internal fun TestProject.applyYarn() {
    buildGradleKts.modify {
        "import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn" +
                it + "\n" +
                """
                rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
                    org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin.apply(rootProject)
                }
                """.trimIndent()
    }
}