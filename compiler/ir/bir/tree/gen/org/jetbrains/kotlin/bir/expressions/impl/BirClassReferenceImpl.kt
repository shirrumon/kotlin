/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions.impl

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.SourceSpan
import org.jetbrains.kotlin.bir.declarations.BirAttributeContainer
import org.jetbrains.kotlin.bir.expressions.BirClassReference
import org.jetbrains.kotlin.bir.symbols.BirClassifierSymbol
import org.jetbrains.kotlin.bir.types.BirType

class BirClassReferenceImpl(
    sourceSpan: SourceSpan,
    type: BirType,
    symbol: BirClassifierSymbol,
    classType: BirType,
) : BirClassReference() {
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

    private var _symbol: BirClassifierSymbol = symbol

    override var symbol: BirClassifierSymbol
        get() = _symbol
        set(value) {
            if (_symbol != value) {
                _symbol = value
                invalidate()
            }
        }

    private var _classType: BirType = classType

    override var classType: BirType
        get() = _classType
        set(value) {
            if (_classType != value) {
                _classType = value
                invalidate()
            }
        }

    override fun replaceChildProperty(old: BirElement, new: BirElement?) {
        when {
            else -> throwChildForReplacementNotFound(old)
        }
    }
}
