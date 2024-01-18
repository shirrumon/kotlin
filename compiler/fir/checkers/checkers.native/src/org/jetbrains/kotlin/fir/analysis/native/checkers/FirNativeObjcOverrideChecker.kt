/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.native.checkers

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.PlatformConflictDeclarationsDiagnosticDispatcher
import org.jetbrains.kotlin.fir.analysis.diagnostics.native.FirNativeErrors
import org.jetbrains.kotlin.fir.backend.native.interop.getObjCMethodInfoFromOverriddenFunctions
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.name.NativeStandardInteropNames.Annotations.objCOverrideClassId

private fun FirFunctionSymbol<*>.isInheritedFromObjc(context: CheckerContext): Boolean {
    return getObjCMethodInfoFromOverriddenFunctions(context.session, context.scopeSession) != null
}

private fun FirFunctionSymbol<*>.hasDifferentParameterNames(other: FirFunctionSymbol<*>) : Boolean {
    return valueParameterSymbols.drop(1).map { it.name } != other.valueParameterSymbols.drop(1).map { it.name }
}

fun NativeConflictDeclarationsDiagnosticDispatcher() = PlatformConflictDeclarationsDiagnosticDispatcher dispatcher@{ declaration, symbols, context ->
    if (declaration is FirFunctionSymbol<*> && symbols.all { it is FirFunctionSymbol<*> }) {
        if (declaration.isInheritedFromObjc(context) && symbols.all { (it as FirFunctionSymbol<*>).isInheritedFromObjc(context) }) {
            if (symbols.all { (it as FirFunctionSymbol<*>).hasDifferentParameterNames(declaration) }) {
                if (declaration.hasAnnotation(objCOverrideClassId, context.session)) {
                    return@dispatcher null
                } else {
                    return@dispatcher FirNativeErrors.CONFLICTING_OBJC_OVERLOADS
                }
            }
        }
    }
    PlatformConflictDeclarationsDiagnosticDispatcher.DEFAULT.getDiagnostic(declaration, symbols, context)
}

object FirNativeObjcOverrideChecker : FirFunctionChecker() {
    override fun check(
        declaration: FirFunction,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (declaration.hasAnnotation(objCOverrideClassId, context.session)) {
            if (!declaration.symbol.isInheritedFromObjc(context)) {
                reporter.reportOn(declaration.getAnnotationByClassId(objCOverrideClassId, context.session)?.source, FirNativeErrors.INAPPLICABLE_OBJC_OVERRIDE, context)
            }
        }
    }
}