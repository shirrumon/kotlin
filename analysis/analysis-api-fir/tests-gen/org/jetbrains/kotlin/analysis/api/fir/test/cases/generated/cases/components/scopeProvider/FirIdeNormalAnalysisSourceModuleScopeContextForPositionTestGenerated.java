/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.test.cases.generated.cases.components.scopeProvider;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analysis.api.fir.test.configurators.AnalysisApiFirTestConfiguratorFactory;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfiguratorFactoryData;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.FrontendKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisSessionMode;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiMode;
import org.jetbrains.kotlin.analysis.api.impl.base.test.cases.components.scopeProvider.AbstractScopeContextForPositionTest;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.analysis.api.GenerateAnalysisApiTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition")
@TestDataPath("$PROJECT_ROOT")
public class FirIdeNormalAnalysisSourceModuleScopeContextForPositionTestGenerated extends AbstractScopeContextForPositionTest {
    @NotNull
    @Override
    public AnalysisApiTestConfigurator getConfigurator() {
        return AnalysisApiFirTestConfiguratorFactory.INSTANCE.createConfigurator(
            new AnalysisApiTestConfiguratorFactoryData(
                FrontendKind.Fir,
                TestModuleKind.Source,
                AnalysisSessionMode.Normal,
                AnalysisApiMode.Ide
            )
        );
    }

    @Test
    public void testAllFilesPresentInScopeContextForPosition() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @Test
    @TestMetadata("contextReceiver.kt")
    public void testContextReceiver() throws Exception {
        runTest("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition/contextReceiver.kt");
    }

    @Test
    @TestMetadata("enumEntry.kt")
    public void testEnumEntry() throws Exception {
        runTest("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition/enumEntry.kt");
    }

    @Test
    @TestMetadata("errorType.kt")
    public void testErrorType() throws Exception {
        runTest("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition/errorType.kt");
    }

    @Test
    @TestMetadata("localTypeScope.kt")
    public void testLocalTypeScope() throws Exception {
        runTest("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition/localTypeScope.kt");
    }

    @Test
    @TestMetadata("simpleScopeContextForPosition.kt")
    public void testSimpleScopeContextForPosition() throws Exception {
        runTest("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition/simpleScopeContextForPosition.kt");
    }

    @Test
    @TestMetadata("syntheticPropertiesScope.kt")
    public void testSyntheticPropertiesScope() throws Exception {
        runTest("analysis/analysis-api/testData/components/scopeProvider/scopeContextForPosition/syntheticPropertiesScope.kt");
    }
}
