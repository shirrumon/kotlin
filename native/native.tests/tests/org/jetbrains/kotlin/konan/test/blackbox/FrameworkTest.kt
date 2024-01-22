/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.test.blackbox

import com.intellij.testFramework.TestDataPath
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.test.blackbox.support.*
import org.jetbrains.kotlin.konan.test.blackbox.support.ClassLevelProperty
import org.jetbrains.kotlin.konan.test.blackbox.support.EnforcedProperty
import org.jetbrains.kotlin.konan.test.blackbox.support.TestCase
import org.jetbrains.kotlin.konan.test.blackbox.support.TestCompilerArgs
import org.jetbrains.kotlin.konan.test.blackbox.support.TestModule
import org.jetbrains.kotlin.konan.test.blackbox.support.compilation.*
import org.jetbrains.kotlin.konan.test.blackbox.support.compilation.TestCompilationResult.Companion.assertSuccess
import org.jetbrains.kotlin.konan.test.blackbox.support.group.FirPipeline
import org.jetbrains.kotlin.konan.test.blackbox.support.runner.TestExecutable
import org.jetbrains.kotlin.konan.test.blackbox.support.runner.TestRunCheck
import org.jetbrains.kotlin.konan.test.blackbox.support.runner.TestRunChecks
import org.jetbrains.kotlin.konan.test.blackbox.support.settings.*
import org.jetbrains.kotlin.konan.test.blackbox.support.settings.Binaries
import org.jetbrains.kotlin.konan.test.blackbox.support.settings.KotlinNativeTargets
import org.jetbrains.kotlin.konan.test.blackbox.support.settings.Timeouts
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileWriter

@TestDataPath("\$PROJECT_ROOT")
@EnforcedProperty(ClassLevelProperty.COMPILER_OUTPUT_INTERCEPTOR, "NONE")
@EnforcedProperty(ClassLevelProperty.TEST_MODE, "ONE_STAGE_MULTI_MODULE")
class ClassicFrameworkTest : FrameworkTestBase()

@FirPipeline
@Tag("frontend-fir")
@TestDataPath("\$PROJECT_ROOT")
@EnforcedProperty(ClassLevelProperty.COMPILER_OUTPUT_INTERCEPTOR, "NONE")
@EnforcedProperty(ClassLevelProperty.TEST_MODE, "ONE_STAGE_MULTI_MODULE")
class FirFrameworkTest : FrameworkTestBase()

abstract class FrameworkTestBase : AbstractNativeSimpleTest() {
    private val testSuiteDir = File("native/native.tests/testData/framework")
    private val extras = TestCase.NoTestRunnerExtras("There's no entrypoint in Swift program")
    private val testCompilationFactory = TestCompilationFactory()

    @Test
    fun testValuesGenerics() {
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        val testName = "values_generics"

        val testCase = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, "ValuesGenerics",
            listOf(
                testSuiteDir.resolve(testName).resolve("$testName.kt"),
                testSuiteDir.resolve("objcexport/values.kt"),
            ),
            freeCompilerArgs = TestCompilerArgs(listOf("-opt-in=kotlinx.cinterop.ExperimentalForeignApi"))
        )
        testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase, testRunSettings).result.assertSuccess()

        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testStdlib() {
        val testName = "stdlib"
        val testCase = generateObjCFramework(testName)
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testMultipleFrameworks() {
        // this test might not work with dynamic caches until https://youtrack.jetbrains.com/issue/KT-34262 is fixed.
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        val testName = "multiple"

        val testDir = testSuiteDir.resolve(testName)
        val framework1Dir = testDir.resolve("framework1")
        val sharedDir = testDir.resolve("shared")
        val freeCompilerArgs = TestCompilerArgs(listOf("-Xstatic-framework", "-Xpre-link-caches=enable"))
        val testCase1 = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, "First",
            listOf(
                framework1Dir.resolve("first.kt"),
                framework1Dir.resolve("test.kt"),
                sharedDir.resolve("shared.kt"),
            ),
            freeCompilerArgs
        )
        testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase1, testRunSettings).result.assertSuccess()

        val framework2Dir = testDir.resolve("framework2")
        val testCase2 = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, "Second",
            listOf(
                framework2Dir.resolve("second.kt"),
                framework2Dir.resolve("test.kt"),
                sharedDir.resolve("shared.kt"),
            ),
            freeCompilerArgs
        )
        testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase2, testRunSettings).result.assertSuccess()

        compileAndRunSwift(testName, testCase1)  // testCase1 provides testRun parameters. testCase2 should have the same.
    }

    @Test
    fun testMultipleFrameworksStatic() {
        // this test might not work with dynamic caches until https://youtrack.jetbrains.com/issue/KT-34262 is fixed.
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        val testName = "multiple"

        val testDir = testSuiteDir.resolve(testName)
        val framework1Dir = testDir.resolve("framework1")
        val sharedDir = testDir.resolve("shared")
        val testCase1 = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, "First",
            listOf(
                framework1Dir.resolve("first.kt"),
                framework1Dir.resolve("test.kt"),
                sharedDir.resolve("shared.kt"),
            ),
            freeCompilerArgs = TestCompilerArgs(listOf("-Xstatic-framework", "-Xpre-link-caches=enable"))
        )
        testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase1, testRunSettings).result.assertSuccess()

        val framework2Dir = testDir.resolve("framework2")
        val testCase2 = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, "Second",
            listOf(
                framework2Dir.resolve("second.kt"),
                framework2Dir.resolve("test.kt"),
                sharedDir.resolve("shared.kt"),
            ), freeCompilerArgs = TestCompilerArgs(listOf("-Xstatic-framework", "-Xpre-link-caches=enable"))
        )
        testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase2, testRunSettings).result.assertSuccess()

        compileAndRunSwift(testName, testCase1)
    }

    @Test
    fun testGH3343() {
        val testName = "gh3343"
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        val freeCInteropArgs = TestCompilerArgs(emptyList(), cinteropArgs = listOf("-header", "$testName.h"))
        val interopLibrary = compileCInterop(testName, freeCInteropArgs)
        val testCase = generateObjCFramework(testName, emptyList(), setOf(TestModule.Given(interopLibrary.klibFile)))
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testKT42397() {
        val testName = "kt42397"
        val testCase = generateObjCFramework(testName)
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testKT43517() {
        val testName = "kt43517"
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        val interopLibrary = compileCInterop(testName)

        val testCase = generateObjCFramework(testName, emptyList(), setOf(TestModule.Given(interopLibrary.klibFile)))
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testStacktrace() {
        val testName = "stacktrace"
        Assumptions.assumeFalse(testRunSettings.get<OptimizationMode>() == OptimizationMode.OPT)

        val testCase = generateObjCFramework(testName, listOf("-g"))
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testStacktraceBridges() {
        val testName = "stacktraceBridges"
        Assumptions.assumeFalse(testRunSettings.get<OptimizationMode>() == OptimizationMode.OPT)

        val testCase = generateObjCFramework(testName, listOf("-g"))
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testStacktraceByLibbacktrace() {
        val testName = "stacktraceByLibbacktrace"
        val testCase = generateObjCFramework(testName, listOf("-g", "-Xbinary=sourceInfoType=libbacktrace"))
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testAbstractInstantiation() {
        val testName = "abstractInstantiation"
        val checks = TestRunChecks.Default(testRunSettings.get<Timeouts>().executionTimeout).copy(
            exitCodeCheck = TestRunCheck.ExitCode.Expected(134)
        )
        val testCase = generateObjCFramework(testName, checks = checks)
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testFrameworkBundleId() {
        Assumptions.assumeTrue(testRunSettings.get<KotlinNativeTargets>().testTarget.name.startsWith("mac"))
        val testName = "bundle_id"
        val testDir = testSuiteDir.resolve(testName)
        val freeCompilerArgs = TestCompilerArgs(
            listOf(
                "-Xbinary=bundleVersion=FooBundleVersion",
                "-Xbinary=bundleShortVersionString=FooBundleShortVersionString"
            )
        )
        val testCase = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, testName,
            listOf(
                testDir.resolve("main.kt"),
                testDir.resolve("lib.kt"),
            ),
            freeCompilerArgs
        )
        testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase, testRunSettings).result.assertSuccess()

        val buildDir = testRunSettings.get<Binaries>().testBinariesDir
        val infoPlistContents = buildDir.resolve("$testName.framework/Resources/Info.plist").readText()
        listOf(
            "<key>CFBundleIdentifier</key>\\s*<string>$testName</string>",
            "<key>CFBundleShortVersionString</key>\\s*<string>FooBundleShortVersionString</string>",
            "<key>CFBundleVersion</key>\\s*<string>FooBundleVersion</string>",
        ).forEach {
            assert(infoPlistContents.contains(Regex(it))) {
                "Modulemap does not contain pattern `$it`:\n$infoPlistContents"
            }
        }
    }

    @Test
    fun testForwardDeclarations() {
        val testName = "forwardDeclarations"
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        val interopLibrary = compileCInterop(testName)

        val testCase = generateObjCFramework(testName, emptyList(), setOf(TestModule.Given(interopLibrary.klibFile)))
        compileAndRunSwift(testName, testCase)
    }

    private fun compileCInterop(testName: String, freeCInteropArgs: TestCompilerArgs = TestCompilerArgs.EMPTY) =
        cinteropToLibrary(
            targets = targets,
            defFile = testSuiteDir.resolve(testName).resolve("$testName.def"),
            outputDir = buildDir,
            freeCompilerArgs = freeCInteropArgs
        ).assertSuccess().resultingArtifact

    @Test
    fun testUseFoundationModule() {
        val testName = "use_foundation_module"
        generateObjCFramework(testName)
        val modulemapContents = buildDir.resolve("$testName.framework/Modules/module.modulemap").readText()
        val expectedPattern = "use Foundation"
        assert(modulemapContents.contains(expectedPattern)) {
            "Modulemap must contain `$expectedPattern`:\n$modulemapContents"
        }
    }

    @Test
    fun testKT56233() {
        val testName = "kt56233"
        // test must make huge amount of repetitions to make sure there's no race conditions, so bigger timeout is needed.
        val checks = TestRunChecks.Default(testRunSettings.get<Timeouts>().executionTimeout * 10)
        val testCase = generateObjCFramework(testName, checks = checks)
        val swiftExtraOpts = if (testRunSettings.get<GCScheduler>() != GCScheduler.AGGRESSIVE) listOf() else
            listOf("-D", "AGGRESSIVE_GC")
        compileAndRunSwift(testName, testCase, swiftExtraOpts)
    }

    @Test
    fun testKT57791() {
        val testName = "kt57791"
        val testCase = generateObjCFramework(testName)
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun testPermanentObjects() {
        val testName = "permanentObjects"
        Assumptions.assumeFalse(testRunSettings.get<GCType>() == GCType.NOOP) { "Test requires GC to actually happen" }

        val testCase = generateObjCFramework(testName, listOf("-opt-in=kotlin.native.internal.InternalForKotlinNative"))
        compileAndRunSwift(testName, testCase)
    }

    @Test
    fun objCExportTest() {
        objCExportTestImpl("", emptyList(), emptyList(), false)
    }

    @Test
    fun objCExportTestNoGenerics() {
        objCExportTestImpl("NoGenerics", listOf("-Xno-objc-generics"),
                           listOf("-D", "NO_GENERICS"), false)
    }

    @Test
    fun objCExportTestLegacySuspendUnit() {
        objCExportTestImpl("LegacySuspendUnit", listOf("-Xbinary=unitSuspendFunctionObjCExport=legacy"),
                           listOf("-D", "LEGACY_SUSPEND_UNIT_FUNCTION_EXPORT"), false)
    }

    @Test
    fun objCExportTestNoSwiftMemberNameMangling() {
        objCExportTestImpl("NoSwiftMemberNameMangling", listOf("-Xbinary=objcExportDisableSwiftMemberNameMangling=true"),
                           listOf("-D", "DISABLE_MEMBER_NAME_MANGLING"), false)
    }

    @Test
    fun objCExportTestNoInterfaceMemberNameMangling() {
        objCExportTestImpl("NoInterfaceMemberNameMangling", listOf("-Xbinary=objcExportIgnoreInterfaceMethodCollisions=true"),
                           listOf("-D", "DISABLE_INTERFACE_METHOD_NAME_MANGLING"), false)
    }

    @Test
    fun objCExportTestStatic() {
        objCExportTestImpl("Static", listOf("-Xbinary=objcExportSuspendFunctionLaunchThreadRestriction=none"),
                           listOf("-D", "ALLOW_SUSPEND_ANY_THREAD"), true)
    }

    private fun objCExportTestImpl(
        suffix: String,
        frameworkOpts: List<String>,
        swiftOpts: List<String>,
        isStaticFramework: Boolean,
    ) {
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)
        // Compile a couple of KLIBs
        val library = compileToLibrary(
            testSuiteDir.resolve("objcexport/library"),
            buildDir,
            TestCompilerArgs("-Xshort-module-name=MyLibrary", "-module-name", "org.jetbrains.kotlin.native.test-library"),
            emptyList(),
        )
        val noEnumEntries = compileToLibrary(
            testSuiteDir.resolve("objcexport/noEnumEntries"),
            buildDir,
            TestCompilerArgs(
                "-Xshort-module-name=NoEnumEntriesLibrary", "-XXLanguage:-EnumEntries",
                "-module-name", "org.jetbrains.kotlin.native.test-no-enum-entries-library",
            ),
            emptyList(),
        )

        // Convert KT sources info ObjC framework using two KLIbs
        val ktFiles = testSuiteDir.resolve("objcexport").listFiles { file: File -> file.name.endsWith(".kt") }
        assert(ktFiles != null)
        val frameworkName = "Kt"
        val testCase = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR, extras, "Kt",
            ktFiles!!.toList(),
            freeCompilerArgs = TestCompilerArgs(
                frameworkOpts + listOfNotNull(
                    "-Xstatic-framework".takeIf { isStaticFramework },
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-Xexport-kdoc",
                    "-Xbinary=bundleId=foo.bar",
                    "-Xexport-library=${noEnumEntries.klibFile.absolutePath}",
                    "-module-name", frameworkName,
                )
            ),
            givenDependencies = setOf(TestModule.Given(library.klibFile), TestModule.Given(noEnumEntries.klibFile)),
            checks = TestRunChecks.Default(testRunSettings.get<Timeouts>().executionTimeout * 10),
        )
        val framework =
            testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase, testRunSettings).result.assertSuccess()
        if (!isStaticFramework)
            codesign(framework.resultingArtifact.frameworkDir.absolutePath)

        // compile Swift sources using generated ObjC framework
        val swiftFiles = testSuiteDir.resolve("objcexport").listFiles { file: File -> file.name.endsWith(".swift") }
        assert(swiftFiles != null)
        val swiftExtraOpts = buildList {
            addAll(swiftOpts)
            if (testRunSettings.get<GCScheduler>() == GCScheduler.AGGRESSIVE) {
                add("-D")
                add("AGGRESSIVE_GC")
            }
            if (testRunSettings.get<GCType>() == GCType.NOOP) {
                add("-D")
                add("NOOP_GC")
            }
        }
        val successExecutable = compileSwift(swiftFiles!!.toList(), swiftExtraOpts)
        val testExecutable = TestExecutable(
            successExecutable.resultingArtifact,
            successExecutable.loggedData,
            listOf(TestName("objCExportTest$suffix"))
        )
        runExecutableAndVerify(testCase, testExecutable)

        // check Info.plist for expected bundle identifier
        val plistFName = if (targets.testTarget.family == Family.OSX) "Resources/Info.plist" else "Info.plist"
        val infoPlistContents = buildDir.resolve("$frameworkName.framework/$plistFName").readText()
        assert(infoPlistContents.contains(Regex("<key>CFBundleIdentifier</key>\\s*<string>foo.bar</string>"))) {
            "Modulemap does not contain expected pattern with `foo.bar`:\n$infoPlistContents"
        }
    }

    private fun generateObjCFramework(
        name: String,
        testCompilerArgs: List<String> = emptyList(),
        givenDependencies: Set<TestModule.Given> = emptySet(),
        checks: TestRunChecks = TestRunChecks.Default(testRunSettings.get<Timeouts>().executionTimeout),
    ): TestCase {
        Assumptions.assumeTrue(targets.testTarget.family.isAppleFamily)

        val testCase = generateObjCFrameworkTestCase(
            TestKind.STANDALONE_NO_TR,
            extras,
            name.replaceFirstChar { it.uppercase() },
            listOf(testSuiteDir.resolve(name).resolve("$name.kt")),
            TestCompilerArgs(testCompilerArgs),
            givenDependencies,
            checks = checks,
        )
        val objCFrameworkCompilation = testCompilationFactory.testCaseToObjCFrameworkCompilation(testCase, testRunSettings)
        val success = objCFrameworkCompilation.result.assertSuccess()
        codesign(success.resultingArtifact.frameworkDir.absolutePath)
        return testCase
    }

    private fun compileAndRunSwift(testName: String, testCase: TestCase, swiftExtraOpts: List<String> = emptyList()) {
        val success = compileSwift(testName, swiftExtraOpts)
        val testExecutable = TestExecutable(
            success.resultingArtifact,
            success.loggedData,
            listOf(TestName(testName))
        )
        runExecutableAndVerify(testCase, testExecutable)
    }

    private fun compileSwift(
        name: String,
        swiftExtraOpts: List<String>,
    ): TestCompilationResult.Success<out TestCompilationArtifact.Executable> =
        compileSwift(listOf(testSuiteDir.resolve(name).resolve("$name.swift")), swiftExtraOpts)

    private fun compileSwift(
        testSources: List<File>,
        swiftExtraOpts: List<String>,
    ): TestCompilationResult.Success<out TestCompilationArtifact.Executable> {
        // create a test provider and get main entry point
        val provider = buildDir.resolve("provider.swift")
        FileWriter(provider).use { writer ->
            val providers = testSources
                .map { file ->
                    file.name.toString().removeSuffix(".swift").replaceFirstChar { it.uppercase() }
                }
                .map { "${it}Tests" }

            writer.write(
                """
                |// THIS IS AUTOGENERATED FILE
                |// This method is invoked by the main routine to get a list of tests
                |func registerProviders() {
                |    ${providers.joinToString("\n    ") { "$it()" }}
                |}
                """.trimMargin()
            )
        }

        return SwiftCompilation(
            testRunSettings,
            testSources + listOf(
                provider,
                testSuiteDir.resolve("main.swift")
            ),
            TestCompilationArtifact.Executable(buildDir.resolve("swiftTestExecutable")),
            swiftExtraOpts,
        ).result.assertSuccess()
    }
}
