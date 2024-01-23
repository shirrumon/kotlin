/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.bir.backend.lower

import org.jetbrains.kotlin.bir.backend.BirLoweringPhase
import org.jetbrains.kotlin.bir.backend.jvm.JvmBirBackendContext
import org.jetbrains.kotlin.bir.declarations.BirClass
import org.jetbrains.kotlin.bir.declarations.BirConstructor
import org.jetbrains.kotlin.bir.declarations.BirModuleFragment
import org.jetbrains.kotlin.bir.remove
import org.jetbrains.kotlin.descriptors.ClassKind

context(JvmBirBackendContext)
class BirAnnotationLowering : BirLoweringPhase() {
    private val annotationClasses = registerIndexKey<BirClass>(false) { it.kind == ClassKind.ANNOTATION_CLASS }

    override fun invoke(module: BirModuleFragment) {
        getAllElementsWithIndex(annotationClasses).forEach { annotationClass ->
            annotationClass.declarations
                .filterIsInstance<BirConstructor>()
                .forEach { it.remove() }
        }
    }
}