/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.extensions

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataRegistry
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.resolve.calls.CallInfo
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import kotlin.reflect.KClass

abstract class FirFunctionCallRefinementExtension(session: FirSession) : FirExtension(session) {
    companion object {
        val NAME = FirExtensionPointName("FunctionCallRefinementExtension")
    }

    final override val name: FirExtensionPointName
        get() = NAME


    final override val extensionType: KClass<out FirExtension> = FirFunctionCallRefinementExtension::class

    /**
     * Allows a call to be completed with more specific type than declared return type of function
     * ```
     * interface Container<out T> { }
     * fun Container<T>.add(item: String): Container<Any>
     * ```
     * at call site `Container<Any>` can be modified to become `Container<NewLocalType>`
     * ```
     * container.add("A")
     * ```
     * this `NewLocalType` can be created in [intercept]. It must be later saved into FIR tree in [transform]
     * Generated declarations should be local because this [FirExtension] works at body resolve stage and thus cannot create new top level declarations
     *
     * [transform] implementation needs to generate valid FIR
     * @return null if plugin is not interested in a [symbol]
     */
    abstract fun intercept(callInfo: CallInfo, symbol: FirNamedFunctionSymbol): FirResolvedTypeRef?

    /**
     * @param call to a dummy function that was created with modified [FirResolvedTypeRef] as a result of [intercept].
     * Dummy copy doesn't exist in FIR, it was needed to complete the call.
     * @param originalSymbol [intercept] is called with symbol to a declaration that exists somewhere in FIR: library, project code.
     * The same symbol is [originalSymbol].
     * [transform] needs to generate call to [let] with the same return type as [call]
     * and put all generated declarations used in [FirResolvedTypeRef] in statements.
     */
    abstract fun transform(call: FirFunctionCall, originalSymbol: FirNamedFunctionSymbol): FirFunctionCall

    fun interface Factory : FirExtension.Factory<FirFunctionCallRefinementExtension>
}

val FirExtensionService.callRefinementExtensions: List<FirFunctionCallRefinementExtension> by FirExtensionService.registeredExtensions()

internal class OriginalCallData(val originalSymbol: FirNamedFunctionSymbol, val extension: FirFunctionCallRefinementExtension)

internal object OriginalCallDataKey : FirDeclarationDataKey()

internal var FirDeclaration.originalCallData: OriginalCallData? by FirDeclarationDataRegistry.data(OriginalCallDataKey)