/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.actualizer.checker

import org.jetbrains.kotlin.backend.common.actualizer.reportActualAnnotationsNotMatchExpect
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.util.isFakeOverride
import org.jetbrains.kotlin.resolve.calls.mpp.AbstractExpectActualAnnotationMatchChecker

internal object IrExpectActualAnnotationMatchingChecker : IrExpectActualChecker {

    override fun check(context: IrExpectActualChecker.Context) = with(context) {
        if (!diagnosticsReporter.languageVersionSettings.supportsFeature(LanguageFeature.MultiplatformRestrictions)) {
            return
        }

        for ((expectSymbol, actualSymbol) in matchedExpectToActual.entries) {
            if (expectSymbol is IrTypeParameterSymbol) {
                continue
            }
            // Annotations mismatch isn't reported if the expect declaration is fake override
            // If the expect declaration is fake-override then it means that it was overridden on actual.
            // - If the unwrapped fake-override is an expect declaration then we will have its actualization, and it's overridden.
            //   Regular rules for annotations on overridden declarations would be applied in the platform module
            // - If the unwrapped fake-override is not an expect declaration (but just a regular class in common module) then this regular
            //   class is common supertype of our expect and actual class. Again, regular rules for annotations on overridden declarations
            //   apply
            // In the end, it's always an annotation in super class vs override in inherited class. Regular rules for annotations on
            // overridden declarations apply
            if (expectSymbol.isFakeOverride) {
                continue
            }
            val incompatibility =
                AbstractExpectActualAnnotationMatchChecker.areAnnotationsCompatible(expectSymbol, actualSymbol, matchingContext) ?: continue

            val reportOn = getTypealiasSymbolIfActualizedViaTypealias(expectSymbol.owner as IrDeclaration, classActualizationInfo)
                ?: getContainingActualClassIfFakeOverride(actualSymbol)
                ?: actualSymbol
            diagnosticsReporter.reportActualAnnotationsNotMatchExpect(
                incompatibility.expectSymbol as IrSymbol,
                incompatibility.actualSymbol as IrSymbol,
                incompatibility.type.mapAnnotationType { it.annotationSymbol as IrConstructorCall },
                reportOn,
            )
        }
    }

    private val IrSymbol.isFakeOverride: Boolean
        get() = (owner as IrDeclaration).isFakeOverride

    private fun getContainingActualClassIfFakeOverride(actualSymbol: IrSymbol): IrSymbol? {
        if (!actualSymbol.isFakeOverride) {
            return null
        }
        return getContainingTopLevelClass(actualSymbol.owner as IrDeclaration)?.symbol
    }
}