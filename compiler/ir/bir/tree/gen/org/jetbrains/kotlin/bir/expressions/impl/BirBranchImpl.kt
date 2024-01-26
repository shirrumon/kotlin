/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions.impl

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.BirElementVisitorLite
import org.jetbrains.kotlin.bir.SourceSpan
import org.jetbrains.kotlin.bir.acceptLite
import org.jetbrains.kotlin.bir.expressions.BirBranch
import org.jetbrains.kotlin.bir.expressions.BirExpression

class BirBranchImpl(
    sourceSpan: SourceSpan,
    condition: BirExpression,
    result: BirExpression,
) : BirBranch() {
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

    private var _condition: BirExpression? = condition

    override var condition: BirExpression
        get() {
            recordPropertyRead()
            return _condition ?: throwChildElementRemoved("condition")
        }
        set(value) {
            if (_condition != value) {
                replaceChild(_condition, value)
                _condition = value
                invalidate()
            }
        }

    private var _result: BirExpression? = result

    override var result: BirExpression
        get() {
            recordPropertyRead()
            return _result ?: throwChildElementRemoved("result")
        }
        set(value) {
            if (_result != value) {
                replaceChild(_result, value)
                _result = value
                invalidate()
            }
        }
    init {
        initChild(_condition)
        initChild(_result)
    }

    override fun acceptChildrenLite(visitor: BirElementVisitorLite) {
        _condition?.acceptLite(visitor)
        _result?.acceptLite(visitor)
    }

    override fun replaceChildProperty(old: BirElement, new: BirElement?) {
        when {
            this._condition === old -> this.condition = new as BirExpression
            this._result === old -> this.result = new as BirExpression
            else -> throwChildForReplacementNotFound(old)
        }
    }
}
