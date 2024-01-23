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
import org.jetbrains.kotlin.bir.expressions.BirExpression
import org.jetbrains.kotlin.bir.expressions.BirGetClass
import org.jetbrains.kotlin.bir.types.BirType

class BirGetClassImpl(
    sourceSpan: SourceSpan,
    type: BirType,
    argument: BirExpression,
) : BirGetClass() {
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

    private var _attributeOwnerId: BirAttributeContainer = this

    override var attributeOwnerId: BirAttributeContainer
        get() {
            recordPropertyRead()
            return _attributeOwnerId
        }
        set(value) {
            if (_attributeOwnerId != value) {
                _attributeOwnerId = value
                invalidate()
            }
        }

    private var _type: BirType = type

    override var type: BirType
        get() {
            recordPropertyRead()
            return _type
        }
        set(value) {
            if (_type != value) {
                _type = value
                invalidate()
            }
        }

    private var _argument: BirExpression = argument

    override var argument: BirExpression
        get() {
            recordPropertyRead()
            return _argument
        }
        set(value) {
            if (_argument != value) {
                replaceChild(_argument, value)
                _argument = value
                invalidate()
            }
        }
    init {
        initChild(_argument)
    }

    override fun replaceChildProperty(old: BirElement, new: BirElement?) {
        when {
            this._argument === old -> this.argument = new as BirExpression
            else -> throwChildForReplacementNotFound(old)
        }
    }
}
