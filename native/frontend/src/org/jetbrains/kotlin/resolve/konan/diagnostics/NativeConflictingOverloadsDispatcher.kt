/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.konan.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.ir.objcinterop.getObjCMethodInfo
import org.jetbrains.kotlin.name.NativeStandardInteropNames.Annotations.objCOverrideClassId
import org.jetbrains.kotlin.resolve.ConflictingOverloadsDispatcher

private fun FunctionDescriptor.hasDifferentParameterNames(other: FunctionDescriptor) : Boolean {
    return valueParameters.drop(1).map { it.name } != other.valueParameters.drop(1).map { it.name }
}

object NativeConflictingOverloadsDispatcher : ConflictingOverloadsDispatcher {
    override fun getDiagnostic(
        declaration: DeclarationDescriptor,
        redeclarations: Collection<DeclarationDescriptor>
    ): DiagnosticFactory1<PsiElement, Collection<DeclarationDescriptor>>? {
        if (declaration is FunctionDescriptor && redeclarations.all { it is FunctionDescriptor }) {
            if (declaration.getObjCMethodInfo() != null && redeclarations.all { (it as FunctionDescriptor).getObjCMethodInfo() != null }) {
                if (redeclarations.all { it === declaration || (it as FunctionDescriptor).hasDifferentParameterNames(declaration) }) {
                    if (declaration.annotations.hasAnnotation(objCOverrideClassId.asSingleFqName())) {
                        return null
                    } else {
                        return ErrorsNative.CONFLICTING_OBJC_OVERLOADS
                    }
                }
            }
        }
        return ConflictingOverloadsDispatcher.Default.getDiagnostic(declaration, redeclarations)
    }
}