/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.resolve.extensions;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.analysis.api.GenerateAnalysisApiTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/low-level-api-fir/testData/resolveExtensionDisposal")
@TestDataPath("$PROJECT_ROOT")
public class ResolveExtensionDisposalAfterModificationEventTestGenerated extends AbstractResolveExtensionDisposalAfterModificationEventTest {
    @Test
    @TestMetadata("afterGlobalModuleStateModification.kt")
    public void testAfterGlobalModuleStateModification() throws Exception {
        runTest("analysis/low-level-api-fir/testData/resolveExtensionDisposal/afterGlobalModuleStateModification.kt");
    }

    @Test
    @TestMetadata("afterGlobalSourceModuleStateModification.kt")
    public void testAfterGlobalSourceModuleStateModification() throws Exception {
        runTest("analysis/low-level-api-fir/testData/resolveExtensionDisposal/afterGlobalSourceModuleStateModification.kt");
    }

    @Test
    @TestMetadata("afterGlobalSourceOutOfBlockModification.kt")
    public void testAfterGlobalSourceOutOfBlockModification() throws Exception {
        runTest("analysis/low-level-api-fir/testData/resolveExtensionDisposal/afterGlobalSourceOutOfBlockModification.kt");
    }

    @Test
    @TestMetadata("afterModuleOutOfBlockModification.kt")
    public void testAfterModuleOutOfBlockModification() throws Exception {
        runTest("analysis/low-level-api-fir/testData/resolveExtensionDisposal/afterModuleOutOfBlockModification.kt");
    }

    @Test
    @TestMetadata("afterModuleStateModification.kt")
    public void testAfterModuleStateModification() throws Exception {
        runTest("analysis/low-level-api-fir/testData/resolveExtensionDisposal/afterModuleStateModification.kt");
    }

    @Test
    public void testAllFilesPresentInResolveExtensionDisposal() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/low-level-api-fir/testData/resolveExtensionDisposal"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }
}
