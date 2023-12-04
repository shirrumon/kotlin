/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import java.io.File

/**
 * Holds current mapping from source files to fragments, in the K2 KMP context
 *
 * Note: for KT-62686, we don't really care about removed files: if a source isn't recompiled,
 * it can't get illegal lookups.
 *
 * So there's no need to store previous source-to-fragment mapping
 */
class FragmentContext(
    /**
     * Map from path to source to this source's fragment
     */
    private val fileToFragment: Map<String, String>,
    /**
     * If a fragment isn't refined by any other fragments, it's allowed to have incremental compilation
     */
    private val leafFragments: Set<String>
) {
    /**
     * Use of `absolutePath` is coordinated with K2MultiplatformStructure.fragmentSourcesCompilerArgs
     */
    fun dirtySetTouchesNonLeafFragments(dirtySet: Iterable<File>): Boolean {
        //TODO: if you really want to test it in K1 mode, need to either do something silly, or implement
        // the logic on Gradle Plugin side.
        // An example of "something silly" would be `absolutePath.contains("common")`
        // I recommend to just disable IC, though
        return dirtySet.any { file ->
            !leafFragments.contains(fileToFragment[file.absolutePath])
        }
    }

    companion object {
        fun fromCompilerArguments(args: CommonCompilerArguments): FragmentContext? {
            val noFragmentData = listOf(args.fragments, args.fragmentRefines, args.fragmentSources).any {
                it.isNullOrEmpty()
            }
            if (noFragmentData) {
                return null
            }

            val fileToFragment = args.fragmentSources!!.associate {
                // expected format: -Xfragment-sources=jvmMain:/tmp/<..>/kotlin/main.kt,<...>
                val fragmentSource = it.split(":", limit=2)
                Pair(fragmentSource.last(), fragmentSource.first())
            }
            val refinedFragments = args.fragmentRefines!!.map { fragmentRefine ->
                // expected format: -Xfragment-refines=jvmMain:commonMain,<...>
                fragmentRefine.split(":", limit = 2).last()
            }.toSet()
            val leafFragments = args.fragments!!.toSet() - refinedFragments

            return FragmentContext(fileToFragment, leafFragments)
        }
    }
}