/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.LowMemoryWatcher
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.CachedValueBase
import org.jetbrains.kotlin.analysis.api.KtAnalysisApiInternals
import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.api.lifetime.KtLifetimeToken
import org.jetbrains.kotlin.analysis.api.lifetime.KtReadActionConfinementLifetimeToken
import org.jetbrains.kotlin.analysis.api.session.KtAnalysisSessionProvider
import org.jetbrains.kotlin.analysis.low.level.api.fir.LLFirInternals
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.getFirResolveSession
import org.jetbrains.kotlin.analysis.low.level.api.fir.file.structure.LLFirDeclarationModificationService
import org.jetbrains.kotlin.analysis.project.structure.KtDanglingFileModule
import org.jetbrains.kotlin.analysis.project.structure.KtModule
import org.jetbrains.kotlin.analysis.project.structure.ProjectStructureProvider
import org.jetbrains.kotlin.analysis.project.structure.isStable
import org.jetbrains.kotlin.psi.KtElement
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass

@OptIn(KtAnalysisApiInternals::class)
class KtFirAnalysisSessionProvider(project: Project) : KtAnalysisSessionProvider(project) {
    private val cache: ConcurrentMap<KtModule, CachedValue<KtAnalysisSession>> = ConcurrentHashMap()

    init {
        LowMemoryWatcher.register(::clearCaches, project)
    }

    override fun getAnalysisSession(useSiteKtElement: KtElement): KtAnalysisSession {
        val module = ProjectStructureProvider.getModule(project, useSiteKtElement, contextualModule = null)
        return getAnalysisSessionByUseSiteKtModule(module)
    }

    override fun getAnalysisSessionByUseSiteKtModule(useSiteKtModule: KtModule): KtAnalysisSession {
        if (useSiteKtModule is KtDanglingFileModule && !useSiteKtModule.isStable) {
            val firResolveSession = useSiteKtModule.getFirResolveSession(project)
            val validityToken = tokenFactory.create(project, firResolveSession.useSiteFirSession.createValidityTracker())
            return KtFirAnalysisSession.createAnalysisSessionByFirResolveSession(firResolveSession, validityToken)
        }

        val identifier = tokenFactory.identifier
        identifier.flushPendingChanges(project)

        return cache.computeIfAbsent(useSiteKtModule) {
            CachedValuesManager.getManager(project).createCachedValue {
                val firResolveSession = useSiteKtModule.getFirResolveSession(project)
                val validityTracker = firResolveSession.useSiteFirSession.createValidityTracker()
                val validityToken = tokenFactory.create(project, validityTracker)

                CachedValueProvider.Result(
                    KtFirAnalysisSession.createAnalysisSessionByFirResolveSession(firResolveSession, validityToken),
                    validityTracker,
                )
            }
        }.value
    }

    override fun clearCaches() {
        for (cachedValue in cache.values) {
            check(cachedValue is CachedValueBase<*>) {
                "Unsupported 'CachedValue' of type ${cachedValue.javaClass}'"
            }

            cachedValue.clear()
        }
    }
}

private fun KClass<out KtLifetimeToken>.flushPendingChanges(project: Project) {
    if (this == KtReadActionConfinementLifetimeToken::class &&
        KtReadActionConfinementLifetimeToken.allowFromWriteAction.get() &&
        ApplicationManager.getApplication().isWriteAccessAllowed
    ) {
        // We must flush modifications to publish local modifications into FIR tree
        @OptIn(LLFirInternals::class)
        LLFirDeclarationModificationService.getInstance(project).flushModifications()
    }
}
