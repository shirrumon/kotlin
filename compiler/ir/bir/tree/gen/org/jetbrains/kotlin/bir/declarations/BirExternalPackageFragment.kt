/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/bir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.declarations

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.BirElementClass
import org.jetbrains.kotlin.bir.BirElementVisitor
import org.jetbrains.kotlin.bir.symbols.BirExternalPackageFragmentSymbol
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource

/**
 * A leaf IR tree element.
 *
 * Generated from: [org.jetbrains.kotlin.bir.generator.BirTree.externalPackageFragment]
 */
abstract class BirExternalPackageFragment(elementClass: BirElementClass<*>) : BirPackageFragment(elementClass), BirElement, BirExternalPackageFragmentSymbol {
    abstract val containerSource: DeserializedContainerSource?

    override fun <D> acceptChildren(visitor: BirElementVisitor<D>, data: D) {
        declarations.acceptChildren(visitor, data)
    }

    companion object : BirElementClass<BirExternalPackageFragment>(BirExternalPackageFragment::class.java, 29, true)
}
