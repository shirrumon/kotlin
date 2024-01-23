/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.bir.declarations

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.symbols.BirSymbol
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.util.IdSignature

interface BirSymbolOwner : BirElement, BirSymbol {
    override val isBound: Boolean
        get() = true

    override var signature: IdSignature?

    companion object
}