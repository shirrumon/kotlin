/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.PropertiesProvider
import org.jetbrains.kotlin.gradle.plugin.PropertiesProvider.Companion.kotlinPropertiesProvider


/**
 * All Kotlin plugins configure similar or the exact same properties.
 *
 * For KotlinCommonCompilerOptions/KotlinCommonCompilerToolOptions, common conventions are found here.
 *
 * Also see KMP-specific [KotlinCompilationCompilerOptionsFromTargetConfigurator]
 */

internal fun <T : KotlinCommonCompilerOptions> T.configureCommonCompilerOptions(
    project: Project
): T = apply {
    configureLogging(project.logger)
    configureExperimentalTryNext(project.kotlinPropertiesProvider)
}

private fun <T : KotlinCommonCompilerOptions> T.configureLogging(
    logger: Logger
): T = apply {
    verbose.convention(logger.isDebugEnabled)
}

private fun <T : KotlinCommonCompilerOptions> T.configureExperimentalTryNext(
    kotlinProperties: PropertiesProvider
): T = apply {
    languageVersion.convention(
        kotlinProperties.kotlinExperimentalTryNext.map { enabled ->
            @Suppress("TYPE_MISMATCH")
            if (enabled) KotlinVersion.nextKotlinLanguageVersion else null
        }
    )
}

internal val KotlinVersion.Companion.nextKotlinLanguageVersion get() = KotlinVersion.values().first { it > KotlinVersion.DEFAULT }
