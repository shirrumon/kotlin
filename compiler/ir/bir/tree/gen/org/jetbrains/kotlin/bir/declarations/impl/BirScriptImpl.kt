/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.declarations.impl

import org.jetbrains.kotlin.bir.*
import org.jetbrains.kotlin.bir.declarations.BirConstructor
import org.jetbrains.kotlin.bir.declarations.BirScript
import org.jetbrains.kotlin.bir.declarations.BirValueParameter
import org.jetbrains.kotlin.bir.declarations.BirVariable
import org.jetbrains.kotlin.bir.expressions.BirConstructorCall
import org.jetbrains.kotlin.bir.symbols.BirClassSymbol
import org.jetbrains.kotlin.bir.symbols.BirPropertySymbol
import org.jetbrains.kotlin.bir.symbols.BirScriptSymbol
import org.jetbrains.kotlin.bir.types.BirType
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.util.IdSignature
import org.jetbrains.kotlin.name.Name

class BirScriptImpl @ObsoleteDescriptorBasedAPI constructor(
    sourceSpan: SourceSpan,
    @property:ObsoleteDescriptorBasedAPI
    override val descriptor: DeclarationDescriptor?,
    signature: IdSignature?,
    origin: IrDeclarationOrigin,
    name: Name,
    thisReceiver: BirValueParameter?,
    baseClass: BirType?,
    override var providedProperties: List<BirPropertySymbol>,
    resultProperty: BirPropertySymbol?,
    earlierScriptsParameter: BirValueParameter?,
    override var importedScripts: List<BirScriptSymbol>?,
    override var earlierScripts: List<BirScriptSymbol>?,
    targetClass: BirClassSymbol?,
    constructor: BirConstructor?,
) : BirScript() {
    override val owner: BirScriptImpl
        get() = this

    private var _sourceSpan: SourceSpan = sourceSpan

    override var sourceSpan: SourceSpan
        get() {
            recordPropertyRead()
            return _sourceSpan
        }
        set(value) {
            if (_sourceSpan != value) {
                _sourceSpan = value
                invalidate()
            }
        }

    private var _signature: IdSignature? = signature

    override var signature: IdSignature?
        get() {
            recordPropertyRead()
            return _signature
        }
        set(value) {
            if (_signature != value) {
                _signature = value
                invalidate()
            }
        }

    override var annotations: BirChildElementList<BirConstructorCall> =
            BirChildElementList(this, 1, false)

    private var _origin: IrDeclarationOrigin = origin

    override var origin: IrDeclarationOrigin
        get() {
            recordPropertyRead()
            return _origin
        }
        set(value) {
            if (_origin != value) {
                _origin = value
                invalidate()
            }
        }

    private var _name: Name = name

    override var name: Name
        get() {
            recordPropertyRead()
            return _name
        }
        set(value) {
            if (_name != value) {
                _name = value
                invalidate()
            }
        }

    override val statements: BirChildElementList<BirStatement> = BirChildElementList(this, 2,
            false)

    private var _thisReceiver: BirValueParameter? = thisReceiver

    override var thisReceiver: BirValueParameter?
        get() {
            recordPropertyRead()
            return _thisReceiver
        }
        set(value) {
            if (_thisReceiver != value) {
                replaceChild(_thisReceiver, value)
                _thisReceiver = value
                invalidate()
            }
        }

    private var _baseClass: BirType? = baseClass

    override var baseClass: BirType?
        get() {
            recordPropertyRead()
            return _baseClass
        }
        set(value) {
            if (_baseClass != value) {
                _baseClass = value
                invalidate()
            }
        }

    override var explicitCallParameters: BirChildElementList<BirVariable> =
            BirChildElementList(this, 3, false)

    override var implicitReceiversParameters: BirChildElementList<BirValueParameter> =
            BirChildElementList(this, 4, false)

    override var providedPropertiesParameters: BirChildElementList<BirValueParameter> =
            BirChildElementList(this, 5, false)

    private var _resultProperty: BirPropertySymbol? = resultProperty

    override var resultProperty: BirPropertySymbol?
        get() {
            recordPropertyRead()
            return _resultProperty
        }
        set(value) {
            if (_resultProperty != value) {
                _resultProperty = value
                invalidate()
            }
        }

    private var _earlierScriptsParameter: BirValueParameter? = earlierScriptsParameter

    override var earlierScriptsParameter: BirValueParameter?
        get() {
            recordPropertyRead()
            return _earlierScriptsParameter
        }
        set(value) {
            if (_earlierScriptsParameter != value) {
                replaceChild(_earlierScriptsParameter, value)
                _earlierScriptsParameter = value
                invalidate()
            }
        }

    private var _targetClass: BirClassSymbol? = targetClass

    override var targetClass: BirClassSymbol?
        get() {
            recordPropertyRead()
            return _targetClass
        }
        set(value) {
            if (_targetClass != value) {
                _targetClass = value
                invalidate()
            }
        }

    private var _constructor: BirConstructor? = constructor

    override var constructor: BirConstructor?
        get() {
            recordPropertyRead()
            return _constructor
        }
        set(value) {
            if (_constructor != value) {
                _constructor = value
                invalidate()
            }
        }
    init {
        initChild(_thisReceiver)
        initChild(_earlierScriptsParameter)
    }

    override fun acceptChildrenLite(visitor: BirElementVisitorLite) {
        annotations.acceptChildrenLite(visitor)
        statements.acceptChildrenLite(visitor)
        _thisReceiver?.acceptLite(visitor)
        explicitCallParameters.acceptChildrenLite(visitor)
        implicitReceiversParameters.acceptChildrenLite(visitor)
        providedPropertiesParameters.acceptChildrenLite(visitor)
        _earlierScriptsParameter?.acceptLite(visitor)
    }

    override fun replaceChildProperty(old: BirElement, new: BirElement?) {
        when {
            this._thisReceiver === old -> this.thisReceiver = new as BirValueParameter
            this._earlierScriptsParameter === old -> this.earlierScriptsParameter = new as
                BirValueParameter
            else -> throwChildForReplacementNotFound(old)
        }
    }

    override fun getChildrenListById(id: Int): BirChildElementList<*> = when(id) {
        1 -> this.annotations
        2 -> this.statements
        3 -> this.explicitCallParameters
        4 -> this.implicitReceiversParameters
        5 -> this.providedPropertiesParameters
        else -> throwChildrenListWithIdNotFound(id)
    }
}
