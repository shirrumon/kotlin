/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildtools.api.tests.compilation

import org.jetbrains.kotlin.buildtools.api.CompilerExecutionStrategyConfiguration
import org.jetbrains.kotlin.buildtools.api.SourcesChanges
import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertCompiledSources
import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertLogContainsPatterns
import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertLogDoesNotContainLines
import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertLogDoesNotContainPatterns
//import org.jetbrains.kotlin.buildtools.api.tests.compilation.assertions.assertOutputFiles
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.BaseCompilationTest
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.DefaultStrategyAgnosticCompilationTest
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.LogLevel
import org.jetbrains.kotlin.buildtools.api.tests.compilation.model.project
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ExampleIncrementalCompilationTest : BaseCompilationTest() {
    @DefaultStrategyAgnosticCompilationTest
    fun singleModuleTest(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project {
            val module1 = module("jvm-module-1")

            module1.compileIncrementally(strategyConfig, SourcesChanges.Unknown)

            val fooKt = module1.sourcesDirectory.resolve("foo.kt")
            fooKt.writeText(fooKt.readText().replace("foo()", "foo(i: Int = 1)"))

            module1.compileIncrementally(
                strategyConfig,
                SourcesChanges.Known(modifiedFiles = listOf(fooKt.toFile()), removedFiles = emptyList()),
            ) {
                assertCompiledSources("foo.kt", "bar.kt")
                assertLogContainsPatterns(LogLevel.DEBUG, ".*Incremental compilation completed".toRegex())
            }
        }
    }

    @DefaultStrategyAgnosticCompilationTest
    fun twoModulesTest(strategyConfig: CompilerExecutionStrategyConfiguration) {
        project {
            val module1 = module("jvm-module-1")
            val module2 = module("jvm-module-2", listOf(module1))

            module1.compileIncrementally(strategyConfig, SourcesChanges.Unknown)
            module2.compileIncrementally(strategyConfig, SourcesChanges.Unknown)

            val barKt = module1.sourcesDirectory.resolve("bar.kt")
            barKt.writeText(barKt.readText().replace("bar()", "bar(i: Int = 1)"))

            module1.compileIncrementally(
                strategyConfig,
                SourcesChanges.Known(modifiedFiles = listOf(barKt.toFile()), removedFiles = emptyList())
            )

            module2.compileIncrementally(
                strategyConfig,
                SourcesChanges.Known(modifiedFiles = emptyList(), removedFiles = emptyList())
            ) {
//                assertLogLinePattern(LogLevel.DEBUG, "Incremental compilation.*")
//                assertExactLogLine(LogLevel.DEBUG, "Incremental compilation completed")
            }
        }
    }
}