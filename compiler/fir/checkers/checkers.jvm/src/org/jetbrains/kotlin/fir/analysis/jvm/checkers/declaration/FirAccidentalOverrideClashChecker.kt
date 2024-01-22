/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.jvm.checkers.declaration

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.directOverriddenFunctions
import org.jetbrains.kotlin.fir.analysis.checkers.unsubstitutedScope
import org.jetbrains.kotlin.fir.analysis.diagnostics.jvm.FirJvmErrors.ACCIDENTAL_OVERRIDE_CLASH_BY_JVM_SIGNATURE
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.isHiddenToOvercomeSignatureClash
import org.jetbrains.kotlin.fir.initialSignatureAttr
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.scopes.jvm.computeJvmSignature
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.load.java.SpecialGenericSignatures.Companion.JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP
import org.jetbrains.kotlin.load.java.SpecialGenericSignatures.Companion.sameAsBuiltinMethodWithErasedValueParameters

object FirAccidentalOverrideClashChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {
    override fun check(
        declaration: FirSimpleFunction,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val name = declaration.name
        if (name !in namesPossibleForRenamedBuiltin && !name.sameAsBuiltinMethodWithErasedValueParameters) return
        val containingClass = declaration.getContainingClass(context.session) ?: return

        var hiddenSymbol: FirNamedFunctionSymbol? = null
        containingClass.unsubstitutedScope(context).processFunctionsByName(name) {
            @OptIn(SymbolInternals::class)
            if (it.fir.isHiddenToOvercomeSignatureClash == true) {
                hiddenSymbol = it
            }
        }
        if (hiddenSymbol == null) return
        for (overriddenSymbol in declaration.symbol.directOverriddenFunctions(context)) {
            @OptIn(SymbolInternals::class)
            if (overriddenSymbol.fir.computeJvmSignature() == hiddenSymbol!!.fir.computeJvmSignature()) {
                reporter.reportOn(
                    declaration.source, ACCIDENTAL_OVERRIDE_CLASH_BY_JVM_SIGNATURE, overriddenSymbol, hiddenSymbol!!, context
                )
                return
            }
        }
    }

    private val namesPossibleForRenamedBuiltin = JVM_SHORT_NAME_TO_BUILTIN_SHORT_NAMES_MAP.values.toSet()

    private fun FirSimpleFunction.extractInitialSignatureOrSelf(): FirSimpleFunction =
        (initialSignatureAttr as FirSimpleFunction?)?.extractInitialSignatureOrSelf() ?: this
}
