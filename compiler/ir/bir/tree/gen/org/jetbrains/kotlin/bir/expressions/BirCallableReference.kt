/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions

import org.jetbrains.kotlin.bir.BirElementVisitor
import org.jetbrains.kotlin.bir.accept
import org.jetbrains.kotlin.bir.symbols.BirSymbol

/**
 * A non-leaf IR tree element.
 *
 * Generated from: [org.jetbrains.kotlin.bir.generator.BirTree.callableReference]
 */
abstract class BirCallableReference<S : BirSymbol> : BirMemberAccessExpression<S>() {
    abstract override var symbol: S

    override fun <D> acceptChildren(visitor: BirElementVisitor<D>, data: D) {
        dispatchReceiver?.accept(data, visitor)
        extensionReceiver?.accept(data, visitor)
        valueArguments.acceptChildren(visitor, data)
    }

    companion object
}
