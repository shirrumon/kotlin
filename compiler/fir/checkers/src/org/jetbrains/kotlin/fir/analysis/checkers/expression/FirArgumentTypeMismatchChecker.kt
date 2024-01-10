/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.expression

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.isSubtypeForTypeMismatch
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.resolvedArgumentMapping
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.typeContext

object FirArgumentTypeMismatchChecker : FirFunctionCallChecker() {
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val resolvedArgumentMapping = expression.resolvedArgumentMapping ?: return

        val typeContext = context.session.typeContext
        for ((arg, param) in resolvedArgumentMapping) {
            if (!isSubtypeForTypeMismatch(typeContext, arg.resolvedType, param.returnTypeRef.coneType)) {
                reporter.reportOn(
                    arg.source,
                    FirErrors.ARGUMENT_TYPE_MISMATCH,
                    arg.resolvedType,
                    param.returnTypeRef.coneType,
                    false,
                    context
                )
            }
        }
    }
}