/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.backend.generators

import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.backend.Fir2IrComponents
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall

/**
 * A generator that converts annotations in [FirAnnotationContainer] to annotations in [IrMutableAnnotationContainer].
 *
 * Annotations are bound to the target already in frontend, e.g.
 *
 * +  Annotations on primary constructor properties are already split between value parameters, properties and backing fields in FIR.</li>
 * +  Annotations on regular properties are also already split between properties and backing fields.</li>
 *
 * So this class task is only to convert FirAnnotations to IrAnnotations.
 * Some time before, it performed also annotation splitting between use-site targets.
 */
class AnnotationGenerator(private val components: Fir2IrComponents) : Fir2IrComponents by components {

    fun List<FirAnnotation>.toIrAnnotations(): List<IrConstructorCall> =
        mapNotNull {
            callGenerator.convertToIrConstructorCall(it) as? IrConstructorCall
        }

    fun generate(irClass: IrClass, firClass: FirClass) {
        generateBase(irClass, firClass)
        generateAnnotationsForTypeParameters(irClass, firClass)
    }

    fun generate(irTypeAlias: IrTypeAlias, firTypeAlias: FirTypeAlias) {
        generateBase(irTypeAlias, firTypeAlias)
        generateAnnotationsForTypeParameters(irTypeAlias, firTypeAlias)
    }

    fun generate(irEnumEntry: IrEnumEntry, firEnumEntry: FirEnumEntry) {
        generateBase(irEnumEntry, firEnumEntry)
    }

    fun generate(irProperty: IrProperty, firProperty: FirProperty) {
        generateBase(irProperty, firProperty)
        firProperty.getter?.let { firGetter ->
            irProperty.getter?.let { irGetter ->
                generate(irGetter, firGetter)
            }
        }
        firProperty.setter?.let { firSetter ->
            irProperty.setter?.let { irSetter ->
                generate(irSetter, firSetter)
            }
        }
        firProperty.backingField?.let { firBackingField ->
            irProperty.backingField?.let { generateBase(it, firBackingField) }
        }
    }

    fun generate(irProperty: IrLocalDelegatedProperty, firProperty: FirProperty) {
        generateBase(irProperty, firProperty)
    }

    fun generate(irFunction: IrFunction, firFunction: FirFunction) {
        generateBase(irFunction, firFunction)
        generateAnnotationsForValueParameters(irFunction, firFunction)
        generateAnnotationsForTypeParameters(irFunction, firFunction)
    }

    fun generate(irField: IrField, firField: FirField) {
        generateBase(irField, firField)
    }

    fun generate(irField: IrField, firField: FirBackingField) {
        generateBase(irField, firField)
    }

    fun generate(irParameter: IrValueParameter, firParameter: FirReceiverParameter) {
        generateBase(irParameter, firParameter)
    }

    fun generate(irParameter: IrValueParameter, firParameter: FirValueParameter) {
        generateBase(irParameter, firParameter)
    }

    fun generate(irFile: IrFile, firFile: FirFile) {
        generateBase(irFile, firFile)
    }

    fun generate(irVariable: IrVariable, firProperty: FirProperty) {
        generateBase(irVariable, firProperty)
    }

    private fun generateBase(irContainer: IrMutableAnnotationContainer, firContainer: FirAnnotationContainer) {
        irContainer.annotations = firContainer.annotations.toIrAnnotations()
    }

    private fun generateAnnotationsForValueParameters(irFunction: IrFunction, firFunction: FirFunction) {
        for ((irParameter, firParameter) in irFunction.valueParameters.zip(firFunction.valueParameters)) {
            generate(irParameter, firParameter)
        }
        generateAnnotationsForTypeParameters(irFunction, firFunction)
    }

    private fun generateAnnotationsForTypeParameters(irDeclaration: IrTypeParametersContainer, firDeclaration: FirTypeParameterRefsOwner) {
        for ((irParameter, firParameter) in irDeclaration.typeParameters.zip(firDeclaration.typeParameters)) {
            if (firParameter !is FirTypeParameter) continue
            generateBase(irParameter, firParameter)
        }
    }
}
