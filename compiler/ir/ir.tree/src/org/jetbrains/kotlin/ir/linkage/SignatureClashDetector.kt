/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.linkage

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory1
import org.jetbrains.kotlin.ir.IrDiagnosticReporter
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.fileOrNull
import org.jetbrains.kotlin.ir.util.sourceElement
import org.jetbrains.kotlin.utils.SmartSet

/**
 * Collects the information about declarations and their signatures and reports cases when multiple declarations have the same signature.
 */
abstract class SignatureClashDetector<Signature : Any, Declaration : IrDeclaration> {
    private val declarationsBySignature: HashMap<Signature, MutableSet<Declaration>> = LinkedHashMap()

    /**
     * Returns all the declarations that have [signature] previously recorded by [trackDeclaration].
     */
    protected fun declarationsWithSignature(signature: Signature): Set<Declaration> =
        declarationsBySignature[signature] ?: emptySet()

    /**
     * Records the declaration, so it could later participate in signature clash detection.
     */
    fun trackDeclaration(declaration: Declaration, rawSignature: Signature) {
        declarationsBySignature.computeIfAbsent(rawSignature) { SmartSet.create() }.add(declaration)
    }

    /**
     * Invoked by [reportErrorsTo] whenever at least two declarations with the same [signature] are detected.
     *
     * Use [reportSignatureClashTo] in the implementation to report a diagnostic.
     */
    protected abstract fun reportSignatureConflict(
        signature: Signature,
        declarations: Collection<Declaration>,
        diagnosticReporter: IrDiagnosticReporter,
    )

    /**
     * Reports all detected signature clashes.
     */
    open fun reportErrorsTo(diagnosticReporter: IrDiagnosticReporter) {
        for ((signature, declarations) in declarationsBySignature) {
            if (declarations.size <= 1) continue
            reportSignatureConflict(signature, declarations, diagnosticReporter)
        }
    }

    protected inline fun <Data : Any, ConflictingDeclaration : IrDeclaration> reportSignatureClashTo(
        diagnosticReporter: IrDiagnosticReporter,
        diagnosticFactory: KtDiagnosticFactory1<Data>,
        declarations: Collection<ConflictingDeclaration>,
        data: Data,
        reportOnIfSynthetic: (ConflictingDeclaration) -> IrElement?,
    ) {
        declarations.mapNotNullTo(LinkedHashSet()) { declaration ->
            // Declarations that come from other modules may not have a file, so we don't show diagnostics on them.
            declaration.fileOrNull?.let { file ->
                val reportOn = declaration.takeUnless { it.startOffset < 0 } ?: reportOnIfSynthetic(declaration)
                reportOn?.let { diagnosticReporter.at(it.sourceElement(), it, file) }
            }
        }.forEach {
            it.report(diagnosticFactory, data)
        }
    }
}