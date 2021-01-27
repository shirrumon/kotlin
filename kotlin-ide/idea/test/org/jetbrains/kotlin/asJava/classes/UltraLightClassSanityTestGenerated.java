/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.classes;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.jetbrains.kotlin.test.TestRoot;
import org.junit.runner.RunWith;

/*
 * This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}.
 * DO NOT MODIFY MANUALLY.
 */
@SuppressWarnings("all")
@TestRoot("idea")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
@TestMetadata("testData/compiler/asJava/lightClasses")
public class UltraLightClassSanityTestGenerated extends AbstractUltraLightClassSanityTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("AnnotatedParameterInEnumConstructor.kt")
    public void testAnnotatedParameterInEnumConstructor() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/AnnotatedParameterInEnumConstructor.kt");
    }

    @TestMetadata("AnnotatedParameterInInnerClassConstructor.kt")
    public void testAnnotatedParameterInInnerClassConstructor() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/AnnotatedParameterInInnerClassConstructor.kt");
    }

    @TestMetadata("AnnotatedPropertyWithSites.kt")
    public void testAnnotatedPropertyWithSites() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/AnnotatedPropertyWithSites.kt");
    }

    @TestMetadata("AnnotationClass.kt")
    public void testAnnotationClass() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/AnnotationClass.kt");
    }

    @TestMetadata("Constructors.kt")
    public void testConstructors() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/Constructors.kt");
    }

    @TestMetadata("DataClassWithCustomImplementedMembers.kt")
    public void testDataClassWithCustomImplementedMembers() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/DataClassWithCustomImplementedMembers.kt");
    }

    @TestMetadata("DelegatedNested.kt")
    public void testDelegatedNested() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/DelegatedNested.kt");
    }

    @TestMetadata("Delegation.kt")
    public void testDelegation() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/Delegation.kt");
    }

    @TestMetadata("DeprecatedEnumEntry.kt")
    public void testDeprecatedEnumEntry() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/DeprecatedEnumEntry.kt");
    }

    @TestMetadata("DeprecatedNotHiddenInClass.kt")
    public void testDeprecatedNotHiddenInClass() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/DeprecatedNotHiddenInClass.kt");
    }

    @TestMetadata("DollarsInName.kt")
    public void testDollarsInName() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/DollarsInName.kt");
    }

    @TestMetadata("DollarsInNameNoPackage.kt")
    public void testDollarsInNameNoPackage() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/DollarsInNameNoPackage.kt");
    }

    @TestMetadata("ExtendingInterfaceWithDefaultImpls.kt")
    public void testExtendingInterfaceWithDefaultImpls() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/ExtendingInterfaceWithDefaultImpls.kt");
    }

    @TestMetadata("HiddenDeprecated.kt")
    public void testHiddenDeprecated() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/HiddenDeprecated.kt");
    }

    @TestMetadata("HiddenDeprecatedInClass.kt")
    public void testHiddenDeprecatedInClass() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/HiddenDeprecatedInClass.kt");
    }

    @TestMetadata("InheritingInterfaceDefaultImpls.kt")
    public void testInheritingInterfaceDefaultImpls() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/InheritingInterfaceDefaultImpls.kt");
    }

    @TestMetadata("InlineReified.kt")
    public void testInlineReified() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/InlineReified.kt");
    }

    @TestMetadata("JvmNameOnMember.kt")
    public void testJvmNameOnMember() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/JvmNameOnMember.kt");
    }

    @TestMetadata("JvmStatic.kt")
    public void testJvmStatic() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/JvmStatic.kt");
    }

    @TestMetadata("NestedObjects.kt")
    public void testNestedObjects() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/NestedObjects.kt");
    }

    @TestMetadata("NonDataClassWithComponentFunctions.kt")
    public void testNonDataClassWithComponentFunctions() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/NonDataClassWithComponentFunctions.kt");
    }

    @TestMetadata("OnlySecondaryConstructors.kt")
    public void testOnlySecondaryConstructors() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/OnlySecondaryConstructors.kt");
    }

    @TestMetadata("PublishedApi.kt")
    public void testPublishedApi() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/PublishedApi.kt");
    }

    @TestMetadata("SpecialAnnotationsOnAnnotationClass.kt")
    public void testSpecialAnnotationsOnAnnotationClass() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/SpecialAnnotationsOnAnnotationClass.kt");
    }

    @TestMetadata("StubOrderForOverloads.kt")
    public void testStubOrderForOverloads() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/StubOrderForOverloads.kt");
    }

    @TestMetadata("TypePararametersInClass.kt")
    public void testTypePararametersInClass() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/TypePararametersInClass.kt");
    }

    @TestMetadata("VarArgs.kt")
    public void testVarArgs() throws Exception {
        runTest("testData/compiler/asJava/lightClasses/VarArgs.kt");
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/compilationErrors")
    public static class CompilationErrors extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("ActualClass.kt")
        public void testActualClass() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ActualClass.kt");
        }

        @TestMetadata("ActualTypeAlias.kt")
        public void testActualTypeAlias() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ActualTypeAlias.kt");
        }

        @TestMetadata("ActualTypeAliasCustomJvmPackageName.kt")
        public void testActualTypeAliasCustomJvmPackageName() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ActualTypeAliasCustomJvmPackageName.kt");
        }

        @TestMetadata("AllInlineOnly.kt")
        public void testAllInlineOnly() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/AllInlineOnly.kt");
        }

        @TestMetadata("AnnotationModifiers.kt")
        public void testAnnotationModifiers() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/AnnotationModifiers.kt");
        }

        @TestMetadata("EnumNameOverride.kt")
        public void testEnumNameOverride() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/EnumNameOverride.kt");
        }

        @TestMetadata("ExpectClass.kt")
        public void testExpectClass() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ExpectClass.kt");
        }

        @TestMetadata("ExpectObject.kt")
        public void testExpectObject() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ExpectObject.kt");
        }

        @TestMetadata("ExpectedNestedClass.kt")
        public void testExpectedNestedClass() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ExpectedNestedClass.kt");
        }

        @TestMetadata("ExpectedNestedClassInObject.kt")
        public void testExpectedNestedClassInObject() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/ExpectedNestedClassInObject.kt");
        }

        @TestMetadata("JvmPackageName.kt")
        public void testJvmPackageName() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/JvmPackageName.kt");
        }

        @TestMetadata("LocalInAnnotation.kt")
        public void testLocalInAnnotation() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/LocalInAnnotation.kt");
        }

        @TestMetadata("PrivateInTrait.kt")
        public void testPrivateInTrait() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/PrivateInTrait.kt");
        }

        @TestMetadata("RepetableAnnotations.kt")
        public void testRepetableAnnotations() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/RepetableAnnotations.kt");
        }

        @TestMetadata("SameName.kt")
        public void testSameName() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/SameName.kt");
        }

        @TestMetadata("TopLevelDestructuring.kt")
        public void testTopLevelDestructuring() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/TopLevelDestructuring.kt");
        }

        @TestMetadata("TraitClassObjectField.kt")
        public void testTraitClassObjectField() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/TraitClassObjectField.kt");
        }

        @TestMetadata("TwoOverrides.kt")
        public void testTwoOverrides() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/TwoOverrides.kt");
        }

        @TestMetadata("WrongAnnotations.kt")
        public void testWrongAnnotations() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/compilationErrors/WrongAnnotations.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/delegation")
    public static class Delegation extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("Function.kt")
        public void testFunction() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/delegation/Function.kt");
        }

        @TestMetadata("Property.kt")
        public void testProperty() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/delegation/Property.kt");
        }

        @TestMetadata("WithPlatformTypes.NoCompile.kt")
        public void testWithPlatformTypes_NoCompile() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/delegation/WithPlatformTypes.NoCompile.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/facades")
    public static class Facades extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("AllPrivate.kt")
        public void testAllPrivate() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/facades/AllPrivate.kt");
        }

        @TestMetadata("EmptyFile.NoCompile.kt")
        public void testEmptyFile_NoCompile() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/facades/EmptyFile.NoCompile.kt");
        }

        @TestMetadata("MultiFile.kt")
        public void testMultiFile() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/facades/MultiFile.kt");
        }

        @TestMetadata("SingleFile.kt")
        public void testSingleFile() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/facades/SingleFile.kt");
        }

        @TestMetadata("SingleJvmClassName.kt")
        public void testSingleJvmClassName() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/facades/SingleJvmClassName.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/ideRegression")
    public static class IdeRegression extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("AllOpenAnnotatedClasses.kt")
        public void testAllOpenAnnotatedClasses() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/AllOpenAnnotatedClasses.kt");
        }

        @TestMetadata("ImplementingCharSequenceAndNumber.kt")
        public void testImplementingCharSequenceAndNumber() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/ImplementingCharSequenceAndNumber.kt");
        }

        @TestMetadata("ImplementingMap.kt")
        public void testImplementingMap() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/ImplementingMap.kt");
        }

        @TestMetadata("ImplementingMutableSet.kt")
        public void testImplementingMutableSet() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/ImplementingMutableSet.kt");
        }

        @TestMetadata("InheritingInterfaceDefaultImpls.kt")
        public void testInheritingInterfaceDefaultImpls() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/InheritingInterfaceDefaultImpls.kt");
        }

        @TestMetadata("OverridingFinalInternal.kt")
        public void testOverridingFinalInternal() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/OverridingFinalInternal.kt");
        }

        @TestMetadata("OverridingFinalInternal.extra.kt")
        public void testOverridingFinalInternal_extra() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/OverridingFinalInternal.extra.kt");
        }

        @TestMetadata("OverridingInternal.kt")
        public void testOverridingInternal() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/OverridingInternal.kt");
        }

        @TestMetadata("OverridingInternal.extra.kt")
        public void testOverridingInternal_extra() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/OverridingInternal.extra.kt");
        }

        @TestMetadata("OverridingProtected.kt")
        public void testOverridingProtected() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/OverridingProtected.kt");
        }

        @TestMetadata("OverridingProtected.extra.kt")
        public void testOverridingProtected_extra() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/ideRegression/OverridingProtected.extra.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/nullabilityAnnotations")
    public static class NullabilityAnnotations extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("Class.kt")
        public void testClass() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/Class.kt");
        }

        @TestMetadata("ClassObjectField.kt")
        public void testClassObjectField() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/ClassObjectField.kt");
        }

        @TestMetadata("ClassWithConstructor.kt")
        public void testClassWithConstructor() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/ClassWithConstructor.kt");
        }

        @TestMetadata("ClassWithConstructorAndProperties.kt")
        public void testClassWithConstructorAndProperties() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/ClassWithConstructorAndProperties.kt");
        }

        @TestMetadata("FileFacade.kt")
        public void testFileFacade() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/FileFacade.kt");
        }

        @TestMetadata("Generic.kt")
        public void testGeneric() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/Generic.kt");
        }

        @TestMetadata("IntOverridesAny.kt")
        public void testIntOverridesAny() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/IntOverridesAny.kt");
        }

        @TestMetadata("JvmOverloads.kt")
        public void testJvmOverloads() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/JvmOverloads.kt");
        }

        @TestMetadata("NullableUnitReturn.kt")
        public void testNullableUnitReturn() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/NullableUnitReturn.kt");
        }

        @TestMetadata("OverrideAnyWithUnit.kt")
        public void testOverrideAnyWithUnit() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/OverrideAnyWithUnit.kt");
        }

        @TestMetadata("PlatformTypes.kt")
        public void testPlatformTypes() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/PlatformTypes.kt");
        }

        @TestMetadata("Primitives.kt")
        public void testPrimitives() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/Primitives.kt");
        }

        @TestMetadata("PrivateInClass.kt")
        public void testPrivateInClass() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/PrivateInClass.kt");
        }

        @TestMetadata("Synthetic.kt")
        public void testSynthetic() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/Synthetic.kt");
        }

        @TestMetadata("Trait.kt")
        public void testTrait() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/Trait.kt");
        }

        @TestMetadata("UnitAsGenericArgument.kt")
        public void testUnitAsGenericArgument() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/UnitAsGenericArgument.kt");
        }

        @TestMetadata("UnitParameter.kt")
        public void testUnitParameter() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/UnitParameter.kt");
        }

        @TestMetadata("VoidReturn.kt")
        public void testVoidReturn() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/nullabilityAnnotations/VoidReturn.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/object")
    public static class Object extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("SimpleObject.kt")
        public void testSimpleObject() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/object/SimpleObject.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/publicField")
    public static class PublicField extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("CompanionObject.kt")
        public void testCompanionObject() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/publicField/CompanionObject.kt");
        }

        @TestMetadata("Simple.kt")
        public void testSimple() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/publicField/Simple.kt");
        }
    }

    @RunWith(JUnit3RunnerWithInners.class)
    @TestMetadata("testData/compiler/asJava/lightClasses/script")
    public static class Script extends AbstractUltraLightClassSanityTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        @TestMetadata("HelloWorld.kts")
        public void testHelloWorld() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/script/HelloWorld.kts");
        }

        @TestMetadata("InnerClasses.kts")
        public void testInnerClasses() throws Exception {
            runTest("testData/compiler/asJava/lightClasses/script/InnerClasses.kts");
        }
    }
}
