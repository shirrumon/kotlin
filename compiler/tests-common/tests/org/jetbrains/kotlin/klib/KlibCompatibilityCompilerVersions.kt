/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.klib

enum class KlibCompatibilityCompilerVersions(val value: String) {
    CURRENT("master"),
    NEXT_RELEASE("2.0.0-Beta2"),
    LATEST_RELEASE("1.9.20"),
    OLDEST_SUPPORTED("1.4.21")
}


enum class KlibCompatibilityCompilerVersionsScenario(val value1: KlibCompatibilityCompilerVersions, val value2: KlibCompatibilityCompilerVersions) {
    BackwardNext(KlibCompatibilityCompilerVersions.CURRENT, KlibCompatibilityCompilerVersions.NEXT_RELEASE),
    BackwardLatestRelease(KlibCompatibilityCompilerVersions.CURRENT, KlibCompatibilityCompilerVersions.LATEST_RELEASE),
//    BackwardOldestSupported(KlibCompatibilityCompilerVersions.CURRENT, KlibCompatibilityCompilerVersions.OLDEST_SUPPORTED),
//    ForwardNext(KlibCompatibilityCompilerVersions.NEXT_RELEASE, KlibCompatibilityCompilerVersions.CURRENT),
}