/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.transformers

import org.jetbrains.kotlin.analysis.low.level.api.fir.api.targets.LLFirResolveTarget
import org.jetbrains.kotlin.analysis.low.level.api.fir.file.builder.LLFirLockProvider
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.checkExpectForActualIsResolved
import org.jetbrains.kotlin.fir.FirElementWithResolveState
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirResolveContextCollector
import org.jetbrains.kotlin.fir.resolve.transformers.mpp.FirExpectActualMatcherTransformer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal object LLFirExpectActualMatcherLazyResolver : LLFirLazyResolver(FirResolvePhase.EXPECT_ACTUAL_MATCHING) {
    override fun resolve(
        target: LLFirResolveTarget,
        lockProvider: LLFirLockProvider,
        session: FirSession,
        scopeSession: ScopeSession,
        towerDataContextCollector: FirResolveContextCollector?,
    ) {
        val resolver = LLFirExpectActualMatchingTargetResolver(target, lockProvider, session, scopeSession)
        resolver.resolveDesignation()
    }

    override fun phaseSpecificCheckIsResolved(target: FirElementWithResolveState) {
        if (target.canHaveExpectCounterPart()) {
            checkExpectForActualIsResolved(target)
        }
    }
}

private class LLFirExpectActualMatchingTargetResolver(
    target: LLFirResolveTarget,
    lockProvider: LLFirLockProvider,
    session: FirSession,
    scopeSession: ScopeSession,
) : LLFirTargetResolver(target, lockProvider, FirResolvePhase.EXPECT_ACTUAL_MATCHING) {
    private val transformer = object : FirExpectActualMatcherTransformer(session, scopeSession) {
        override fun transformRegularClass(regularClass: FirRegularClass, data: Nothing?): FirStatement {
            transformMemberDeclaration(regularClass)
            return regularClass
        }
    }

    override fun doLazyResolveUnderLock(target: FirElementWithResolveState) {
        if (target.canHaveExpectCounterPart()) {
            transformer.transformMemberDeclaration(target)
        }
    }
}

@OptIn(ExperimentalContracts::class)
private fun FirElementWithResolveState.canHaveExpectCounterPart(): Boolean {
    contract {
        returns(true) implies (this@canHaveExpectCounterPart is FirMemberDeclaration)
    }
    if (this is FirDeclaration && origin == FirDeclarationOrigin.ScriptCustomization.ResultProperty) {
        return false // expect/actual are not possible in kts files ScriptGetOrBuildFirTestGenerated
    }
    return this is FirMemberDeclaration && when (this) {
        is FirEnumEntry -> true
        is FirProperty -> true
        is FirConstructor -> true
        is FirSimpleFunction -> true
        is FirRegularClass -> true
        is FirTypeAlias -> true
        else -> false
    }
}
