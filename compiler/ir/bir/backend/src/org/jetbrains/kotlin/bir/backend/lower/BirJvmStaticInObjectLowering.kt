/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.bir.backend.lower

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.accept
import org.jetbrains.kotlin.bir.backend.BirLoweringPhase
import org.jetbrains.kotlin.bir.backend.jvm.JvmBirBackendContext
import org.jetbrains.kotlin.bir.backend.jvm.JvmCachedDeclarations
import org.jetbrains.kotlin.bir.declarations.*
import org.jetbrains.kotlin.bir.expressions.*
import org.jetbrains.kotlin.bir.expressions.impl.BirBlockImpl
import org.jetbrains.kotlin.bir.expressions.impl.BirGetFieldImpl
import org.jetbrains.kotlin.bir.expressions.impl.BirTypeOperatorCallImpl
import org.jetbrains.kotlin.bir.getBackReferences
import org.jetbrains.kotlin.bir.or
import org.jetbrains.kotlin.bir.util.defaultType
import org.jetbrains.kotlin.bir.util.hasAnnotation
import org.jetbrains.kotlin.bir.util.isTrivial
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.resolve.annotations.JVM_STATIC_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.utils.addIfNotNull

context(JvmBirBackendContext)
class BirJvmStaticInObjectLowering : BirLoweringPhase() {
    private val JvmStaticAnnotation by lz { birBuiltIns.findClass(JVM_STATIC_ANNOTATION_FQ_NAME) }

    private val allFunctions = registerIndexKey(BirSimpleFunction, false)
    private val allMemberAccesses = registerIndexKey(BirMemberAccessExpression, false)

    override fun lower(module: BirModuleFragment) {
        getAllElementsWithIndex(allFunctions).forEach { function ->
            val parent = function.parent
            if (parent is BirClass && parent.kind == ClassKind.OBJECT && !parent.isCompanion) {
                if (function.isJvmStaticDeclaration()) {
                    function.removeStaticDispatchReceiver(parent)
                }
            }
        }

        getAllElementsWithIndex(allMemberAccesses).forEach { expression ->
            val callee = expression.symbol.owner
            val parent = callee.parent
            if (parent is BirClass && parent.kind == ClassKind.OBJECT && !parent.isCompanion) {
                if (callee is BirDeclaration && callee.isJvmStaticDeclaration()) {
                    return expression.replaceWithStatic(replaceCallee = null)
                }
            }
        }
    }

    private fun BirDeclaration.isJvmStaticDeclaration(): Boolean = JvmStaticAnnotation?.let { annotation ->
        hasAnnotation(annotation)
                || (this as? BirSimpleFunction)?.correspondingPropertySymbol?.owner?.hasAnnotation(annotation) == true
                || (this as? BirProperty)?.getter?.hasAnnotation(annotation) == true
    } == true

    private fun BirSimpleFunction.removeStaticDispatchReceiver(parentObject: BirClass) {
        dispatchReceiverParameter?.let { oldDispatchReceiverParameter ->
            replaceThisByStaticReference(parentObject, oldDispatchReceiverParameter)
            dispatchReceiverParameter = null
        }
    }

    private fun BirMemberAccessExpression<*>.replaceWithStatic(replaceCallee: BirSimpleFunction?) {
        val receiver = dispatchReceiver ?: return
        dispatchReceiver = null
        if (replaceCallee != null) {
            (this as BirCall).symbol = replaceCallee
        }
        if (receiver.isTrivial()) {
            // Receiver has no side effects (aside from maybe class initialization) so discard it.
            return
        }

        val block = BirBlockImpl(sourceSpan, type, null)
        replaceWith(block)
        block.statements += receiver.coerceToUnit() // evaluate for side effects
        block.statements += this@replaceWithStatic
    }

    private fun BirExpression.coerceToUnit() =
        BirTypeOperatorCallImpl(sourceSpan, birBuiltIns.unitType, IrTypeOperator.IMPLICIT_COERCION_TO_UNIT, this, birBuiltIns.unitType)

    private fun BirElement.replaceThisByStaticReference(
        birClass: BirClass,
        oldThisReceiverParameter: BirValueParameter,
    ) {
        accept { element ->
            if (element is BirGetValue) {
                val new = BirGetFieldImpl(element.sourceSpan, birClass.defaultType, field, null, null, null)
                element.replaceWith(new)
            } else {
                element.walkIntoChildren()
            }
        }
    }
}


