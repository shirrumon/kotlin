/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("JUnitTestCaseWithNoTests")

package org.jetbrains.kotlin.codegen.ir

import org.jetbrains.kotlin.codegen.*
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.TargetBackend.JVM_IR

open class IrPackageGenTest : PackageGenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrPrimitiveTypesTest : PrimitiveTypesTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrAnnotationGenTest : AnnotationGenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrOuterClassGenTest : OuterClassGenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrPropertyGenTest : PropertyGenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

abstract class AbstractIrDumpDeclarationsTest : AbstractDumpDeclarationsTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrKotlinSyntheticClassAnnotationTest : KotlinSyntheticClassAnnotationTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrVarArgTest : VarArgTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrControlStructuresTest : ControlStructuresTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrInnerClassInfoGenTest : InnerClassInfoGenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrMethodOrderTest : MethodOrderTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrReflectionClassLoaderTest : ReflectionClassLoaderTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrCustomBytecodeTextTest : CustomBytecodeTextTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrCustomScriptCodegenTest : CustomScriptCodegenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}

open class IrSourceInfoGenTest : SourceInfoGenTest() {
    override val backend: TargetBackend
        get() = JVM_IR
}
