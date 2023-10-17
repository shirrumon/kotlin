/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions.impl

import org.jetbrains.kotlin.bir.BirChildElementList
import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.SourceSpan
import org.jetbrains.kotlin.bir.declarations.BirAttributeContainer
import org.jetbrains.kotlin.bir.expressions.BirExpression
import org.jetbrains.kotlin.bir.expressions.BirPropertyReference
import org.jetbrains.kotlin.bir.symbols.BirFieldSymbol
import org.jetbrains.kotlin.bir.symbols.BirPropertySymbol
import org.jetbrains.kotlin.bir.symbols.BirSimpleFunctionSymbol
import org.jetbrains.kotlin.bir.types.BirType
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin

class BirPropertyReferenceImpl(
    sourceSpan: SourceSpan,
    type: BirType,
    symbol: BirPropertySymbol,
    dispatchReceiver: BirExpression?,
    extensionReceiver: BirExpression?,
    origin: IrStatementOrigin?,
    override var typeArguments: List<BirType?>,
    field: BirFieldSymbol?,
    getter: BirSimpleFunctionSymbol?,
    setter: BirSimpleFunctionSymbol?,
) : BirPropertyReference() {
    private var _sourceSpan: SourceSpan = sourceSpan

    override var sourceSpan: SourceSpan
        get() = _sourceSpan
        set(value) {
            if (_sourceSpan != value) {
                _sourceSpan = value
                invalidate()
            }
        }

    private var _attributeOwnerId: BirAttributeContainer = this

    override var attributeOwnerId: BirAttributeContainer
        get() = _attributeOwnerId
        set(value) {
            if (_attributeOwnerId != value) {
                _attributeOwnerId = value
                invalidate()
            }
        }

    private var _type: BirType = type

    override var type: BirType
        get() = _type
        set(value) {
            if (_type != value) {
                _type = value
                invalidate()
            }
        }

    private var _symbol: BirPropertySymbol = symbol

    override var symbol: BirPropertySymbol
        get() = _symbol
        set(value) {
            if (_symbol != value) {
                _symbol = value
                invalidate()
            }
        }

    private var _dispatchReceiver: BirExpression? = dispatchReceiver

    override var dispatchReceiver: BirExpression?
        get() = _dispatchReceiver
        set(value) {
            if (_dispatchReceiver != value) {
                replaceChild(_dispatchReceiver, value)
                _dispatchReceiver = value
                invalidate()
            }
        }

    private var _extensionReceiver: BirExpression? = extensionReceiver

    override var extensionReceiver: BirExpression?
        get() = _extensionReceiver
        set(value) {
            if (_extensionReceiver != value) {
                replaceChild(_extensionReceiver, value)
                _extensionReceiver = value
                invalidate()
            }
        }

    private var _origin: IrStatementOrigin? = origin

    override var origin: IrStatementOrigin?
        get() = _origin
        set(value) {
            if (_origin != value) {
                _origin = value
                invalidate()
            }
        }

    override val valueArguments: BirChildElementList<BirExpression?> =
            BirChildElementList(this, 0)

    private var _field: BirFieldSymbol? = field

    override var field: BirFieldSymbol?
        get() = _field
        set(value) {
            if (_field != value) {
                _field = value
                invalidate()
            }
        }

    private var _getter: BirSimpleFunctionSymbol? = getter

    override var getter: BirSimpleFunctionSymbol?
        get() = _getter
        set(value) {
            if (_getter != value) {
                _getter = value
                invalidate()
            }
        }

    private var _setter: BirSimpleFunctionSymbol? = setter

    override var setter: BirSimpleFunctionSymbol?
        get() = _setter
        set(value) {
            if (_setter != value) {
                _setter = value
                invalidate()
            }
        }
    init {
        initChild(_dispatchReceiver)
        initChild(_extensionReceiver)
    }

    override fun replaceChildProperty(old: BirElement, new: BirElement?) {
        when {
            this._dispatchReceiver === old -> this.dispatchReceiver = new as BirExpression
            this._extensionReceiver === old -> this.extensionReceiver = new as BirExpression
            else -> throwChildForReplacementNotFound(old)
        }
    }

    override fun getChildrenListById(id: Int): BirChildElementList<*> = when {
        id == 0 -> this.valueArguments
        else -> throwChildrenListWithIdNotFound(id)
    }
}
