/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.bir

import org.jetbrains.kotlin.backend.common.phaser.*
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.bir.backend.BirLoweringPhase
import org.jetbrains.kotlin.bir.backend.jvm.JvmBirBackendContext
import org.jetbrains.kotlin.bir.backend.jvm.JvmCachedDeclarations
import org.jetbrains.kotlin.bir.backend.lower.*
import org.jetbrains.kotlin.bir.declarations.BirAttributeContainer
import org.jetbrains.kotlin.bir.declarations.BirClass
import org.jetbrains.kotlin.bir.declarations.BirExternalPackageFragment
import org.jetbrains.kotlin.bir.declarations.BirModuleFragment
import org.jetbrains.kotlin.bir.lazy.BirLazyElementBase
import org.jetbrains.kotlin.bir.types.BirType
import org.jetbrains.kotlin.bir.util.Bir2IrConverter
import org.jetbrains.kotlin.bir.util.Ir2BirConverter
import org.jetbrains.kotlin.bir.util.countAllElementsInTree
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrAttributeContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.impl.IrModuleFragmentImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.transformFlat
import java.util.IdentityHashMap
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTimedValue

val allBirPhases = listOf<Pair<(JvmBirBackendContext) -> BirLoweringPhase, List<String>>>(
    ::BirJvmNameLowering to listOf(),
    ::BirJvmStaticInObjectLowering to listOf("JvmStaticInObject"),
    ::BirRepeatedAnnotationLowering to listOf("RepeatedAnnotation"),
    ::BirTypeAliasAnnotationMethodsLowering to listOf("TypeAliasAnnotationMethodsLowering"),
    ::BirProvisionalFunctionExpressionLowering to listOf("FunctionExpression"),
    ::BirJvmOverloadsAnnotationLowering to listOf("JvmOverloadsAnnotation"),
    ::BirMainMethodGenerationLowering to listOf("MainMethodGeneration"),
    ::BirPolymorphicSignatureLowering to listOf("PolymorphicSignature"),
    ::BirVarargLowering to listOf("VarargLowering"),
    ::BirJvmLateinitLowering to listOf("JvmLateinitLowering"),
    ::BirJvmInventNamesForLocalClassesLowering to listOf("InventNamesForLocalClasses"),
    ::BirInlineCallableReferenceToLambdaLowering to listOf("InlineCallableReferenceToLambdaPhase"),
    ::BirDirectInvokeLowering to listOf("DirectInvokes"),
    ::BirAnnotationLowering to listOf("Annotation"),
)

private val excludedPhases = setOf<String>(
    //"Ir2Bir",
    //"Lower Bir",
    "Bir2Ir",

    // This phase removes annotation constructors, but they are still being used,
    // which causes an exception in BIR. It works in IR because removed constructors
    // still have their parent property set.
    //"Annotation",
    // This phase is not implemented, as it is hardly ever relevant.
    "AnnotationImplementation",
)

private val StopCompilationAfterBir = true

class BirCompilation() {
    private val irPhasesTime = mutableMapOf<String, Duration>()
    private val birPhasesTime = mutableMapOf<String, Duration>()
    private var invokedBirCompilation: BirCompilationBundle? = null

    fun lowerWithBir(
        phases: SameTypeNamedCompilerPhase<JvmBackendContext, IrModuleFragment>,
        context: JvmBackendContext,
        irModuleFragment: IrModuleFragment,
    ): IrModuleFragment {
        val newPhases = reconstructPhases(phases)

        var allExcludedPhases = excludedPhases
        if (context.state.configuration.getBoolean(CommonConfigurationKeys.USE_BIR)) {
            val birPhases = listOf<AbstractNamedCompilerPhase<JvmBackendContext, *, *>>(
                ConvertIrToBirPhase(
                    "Ir2Bir",
                    "Convert IR to BIR",
                ),
                NamedCompilerPhase(
                    "Lower Bir",
                    "Experimental phase to test alternative IR architecture",
                    lower = BirLowering,
                ),
                ConvertBirToIrPhase(
                    "Bir2Ir",
                    "Convert lowered BIR back to IR",
                ),
            )
            val index = newPhases.indexOfFirst { (it as AnyNamedPhase).name == "FileClass" } + 1
            if (StopCompilationAfterBir) {
                newPhases.subList(index, newPhases.size).clear()
            }
            newPhases.addAll(
                index,
                birPhases as List<NamedCompilerPhase<JvmBackendContext, IrModuleFragment>>
            )

            allExcludedPhases += allBirPhases.flatMap { it.second }
        }

        val compoundPhase = newPhases.reduce { result, phase -> result then phase }
        val phaseConfig = PhaseConfigBuilder(compoundPhase).apply {
            enabled += compoundPhase.toPhaseMap().values.filter { it.name !in allExcludedPhases }.toSet()
            verbose += context.phaseConfig.verbose
            toDumpStateBefore += context.phaseConfig.toDumpStateBefore
            toDumpStateAfter += context.phaseConfig.toDumpStateAfter
            dumpToDirectory = context.phaseConfig.dumpToDirectory ?: System.getProperty("fir.dump.dir")
            dumpOnlyFqName = context.phaseConfig.dumpOnlyFqName
            checkConditions = context.phaseConfig.checkConditions
            checkStickyConditions = context.phaseConfig.checkStickyConditions
        }.build()

        val result = compoundPhase.invokeToplevel(phaseConfig, context, irModuleFragment)
        invokedBirCompilation?.let { printCompilationTimings(it, irPhasesTime, birPhasesTime) }
        return result
    }

    private fun reconstructPhases(
        phases: SameTypeNamedCompilerPhase<JvmBackendContext, IrModuleFragment>,
    ): MutableList<CompilerPhase<JvmBackendContext, IrModuleFragment, IrModuleFragment>> {
        val standardPhases = phases.getNamedSubphases()
        val newPhases = standardPhases
            .filter { it.first == 1 }
            .map { it.second }
            .toMutableList() as MutableList<AbstractNamedCompilerPhase<JvmBackendContext, IrModuleFragment, IrModuleFragment>>

        newPhases.transformFlat { topPhase ->
            if (topPhase.name == "PerformByIrFile") {
                val filePhases = topPhase.getNamedSubphases()
                    .filter { it.first == 1 }
                    .map { it.second as AbstractNamedCompilerPhase<JvmBackendContext, IrFile, IrFile> }
                    .toMutableList()

                val annotationPhasesRange = filePhases.subList(filePhases.indexOfFirst { it.name == "Annotation" },
                                                               filePhases.indexOfFirst { it.name == "AnnotationImplementation" } + 1)
                val annotationPhases = annotationPhasesRange.toList()
                annotationPhasesRange.clear()
                filePhases.addAll(
                    filePhases.indexOfFirst { it.name == "DirectInvokes" } + 1,
                    annotationPhases
                )

                val lower = CustomPerFileAggregateLoweringPhase(filePhases)
                listOf(NamedCompilerPhase(topPhase.name, topPhase.description, lower = lower))
            } else {
                val lower = object : SameTypeCompilerPhase<JvmBackendContext, IrModuleFragment> {
                    override fun invoke(
                        phaseConfig: PhaseConfigurationService,
                        phaserState: PhaserState<IrModuleFragment>,
                        context: JvmBackendContext,
                        input: IrModuleFragment,
                    ): IrModuleFragment {
                        topPhase.runBefore(phaseConfig, phaserState, context, input)
                        context.inVerbosePhase = phaseConfig.isVerbose(topPhase)

                        dumpOriginalIrPhase(phaseConfig, input, topPhase.name, true)
                        invokePhaseMeasuringTime("IR", topPhase.name) {
                            topPhase.phaseBody(phaseConfig, phaserState, context, input)
                        }
                        dumpOriginalIrPhase(phaseConfig, input, topPhase.name, false)

                        topPhase.runAfter(phaseConfig, phaserState, context, input, input)
                        return input
                    }
                }

                listOf(NamedCompilerPhase(topPhase.name, topPhase.description, lower = lower))
            }
        }

        return newPhases as MutableList<CompilerPhase<JvmBackendContext, IrModuleFragment, IrModuleFragment>>
    }

    inner class CustomPerFileAggregateLoweringPhase(
        private val filePhases: List<AbstractNamedCompilerPhase<JvmBackendContext, IrFile, IrFile>>,
    ) : SameTypeCompilerPhase<JvmBackendContext, IrModuleFragment> {
        override fun invoke(
            phaseConfig: PhaseConfigurationService,
            phaserState: PhaserState<IrModuleFragment>,
            context: JvmBackendContext,
            input: IrModuleFragment,
        ): IrModuleFragment {
            val filePhaserState = phaserState.changePhaserStateType<IrModuleFragment, IrFile>()
            for (filePhase in filePhases) {
                if (phaseConfig.isEnabled(filePhase)) {
                    for (irFile in input.files) {
                        filePhase.runBefore(phaseConfig, filePhaserState, context, irFile)
                    }
                    context.inVerbosePhase = phaseConfig.isVerbose(filePhase)

                    dumpOriginalIrPhase(phaseConfig, input, filePhase.name, true)
                    invokePhaseMeasuringTime(
                        "IR", (filePhase as? AbstractNamedCompilerPhase<*, *, *>)?.name ?: filePhase.javaClass.simpleName
                    ) {
                        for (irFile in input.files) {
                            filePhase.phaseBody(phaseConfig, filePhaserState, context, irFile)
                        }
                    }
                    dumpOriginalIrPhase(phaseConfig, input, filePhase.name, false)

                    for (irFile in input.files) {
                        filePhase.runAfter(phaseConfig, filePhaserState, context, irFile, irFile)
                    }

                    phaserState.alreadyDone.add(filePhase)
                    phaserState.phaseCount++
                } else {
                    for (irFile in input.files) {
                        filePhase.outputIfNotEnabled(phaseConfig, filePhaserState, context, irFile)
                    }
                }
            }

            return input
        }

        override fun getNamedSubphases(startDepth: Int): List<Pair<Int, AbstractNamedCompilerPhase<JvmBackendContext, *, *>>> {
            return filePhases.map { startDepth + 1 to it }
        }
    }

    private inner class ConvertIrToBirPhase(name: String, description: String) :
        SimpleNamedCompilerPhase<JvmBackendContext, IrModuleFragment, BirCompilationBundle>(name, description) {
        override fun phaseBody(context: JvmBackendContext, input: IrModuleFragment): BirCompilationBundle {
            val dynamicPropertyManager = BirElementDynamicPropertyManager()

            val externalModulesBir = BirDatabase()
            val compiledBir = BirDatabase()

            val mappedIr2BirElements = IdentityHashMap<BirElement, IrElement>()
            val ir2BirConverter = Ir2BirConverter(dynamicPropertyManager)
            ir2BirConverter.convertAncestorsForOrphanedElements = true
            ir2BirConverter.appendElementAsDatabaseRoot = { old, new ->
                mappedIr2BirElements[new] = old
                when {
                    old === input -> compiledBir
                    new is BirModuleFragment || new is BirExternalPackageFragment || new is BirLazyElementBase -> externalModulesBir
                    else -> null
                }
            }

            val birContext: JvmBirBackendContext
            val birModule: BirModuleFragment
            invokePhaseMeasuringTime("BIR", "convert IR to BIR") {
                birContext = JvmBirBackendContext(
                    context,
                    input.descriptor,
                    compiledBir,
                    externalModulesBir,
                    ir2BirConverter,
                    dynamicPropertyManager,
                    allBirPhases.map { it.first },
                )

                birModule = ir2BirConverter.remapElement<BirModuleFragment>(input)
            }

            ir2BirConverter.convertImplElementsIntoLazyWhenPossible = true

            val size = birModule.countAllElementsInTree()
            return BirCompilationBundle(
                birModule,
                birContext,
                input,
                mappedIr2BirElements,
                ir2BirConverter.remappedIr2BirTypes,
                dynamicPropertyManager,
                size
            ).also {
                invokedBirCompilation = it
            }
        }

        override fun outputIfNotEnabled(
            phaseConfig: PhaseConfigurationService,
            phaserState: PhaserState<IrModuleFragment>,
            context: JvmBackendContext,
            input: IrModuleFragment,
        ) = BirCompilationBundle(null, null, input, emptyMap(), emptyMap(), null, 0)
    }

    class BirCompilationBundle(
        val birModule: BirModuleFragment?,
        val backendContext: JvmBirBackendContext?,
        val irModuleFragment: IrModuleFragment,
        val mappedIr2BirElements: Map<BirElement, IrElement>,
        val remappedIr2BirTypes: Map<BirType, IrType>,
        val dynamicPropertyManager: BirElementDynamicPropertyManager?,
        val estimatedIrTreeSize: Int,
    )

    private val BirLowering = object : SameTypeCompilerPhase<JvmBackendContext, BirCompilationBundle> {
        override fun invoke(
            phaseConfig: PhaseConfigurationService,
            phaserState: PhaserState<BirCompilationBundle>,
            context: JvmBackendContext,
            input: BirCompilationBundle,
        ): BirCompilationBundle {
            val compiledBir = input.backendContext!!.compiledBir
            val externalBir = input.backendContext.externalModulesBir

            invokePhaseMeasuringTime("BIR", "applyNewRegisteredIndices") {
                compiledBir.activateNewRegisteredIndices()
                externalBir.activateNewRegisteredIndices()
            }
            repeat(1) {
                invokePhaseMeasuringTime("BIR", "baseline tree traversal") {
                    input.birModule!!.countAllElementsInTree()
                }

                invokePhaseMeasuringTime("BIR", "index compiled BIR") {
                    compiledBir.reindexAllElements()
                }
                invokePhaseMeasuringTime("BIR", "index external BIR") {
                    externalBir.reindexAllElements()
                }
                //Thread.sleep(100)
            }

            dumpBirPhase(context, phaseConfig, input, null, "Initial")
            for (phase in input.backendContext.loweringPhases) {
                val phaseName = phase.javaClass.simpleName
                invokePhaseMeasuringTime("BIR", phaseName) {
                    phase.lower(input.birModule!!)
                }

                dumpBirPhase(context, phaseConfig, input, phase, null)
            }

            return input
        }
    }

    private inner class ConvertBirToIrPhase(name: String, description: String) :
        SimpleNamedCompilerPhase<JvmBackendContext, BirCompilationBundle, IrModuleFragment>(name, description) {
        override fun phaseBody(context: JvmBackendContext, input: BirCompilationBundle): IrModuleFragment {
            val dynamicPropertyManager = input.dynamicPropertyManager!!
            val compiledBir = input.birModule!!.getContainingDatabase()!!
            val bir2IrConverter = Bir2IrConverter(
                dynamicPropertyManager,
                input.mappedIr2BirElements,
                context.irBuiltIns,
                compiledBir,
                input.estimatedIrTreeSize
            )
            bir2IrConverter.remappedIr2BirTypes = input.remappedIr2BirTypes

            val localClassType = dynamicPropertyManager.acquireProperty(BirJvmInventNamesForLocalClassesLowering.LocalClassType)
            val fieldForObjectInstanceToken = dynamicPropertyManager.acquireProperty(JvmCachedDeclarations.FieldForObjectInstance)
            val interfaceCompanionFieldDeclaration =
                dynamicPropertyManager.acquireProperty(JvmCachedDeclarations.InterfaceCompanionFieldDeclaration)
            val fieldForObjectInstanceParentToken =
                dynamicPropertyManager.acquireProperty(JvmCachedDeclarations.FieldForObjectInstanceParent)
            bir2IrConverter.elementConvertedCallback = { old, new ->
                if (old is BirAttributeContainer) {
                    old[localClassType]?.let {
                        context.putLocalClassType(new as IrAttributeContainer, it)
                    }
                }
                if (old is BirClass) {
                    new as IrClass
                    old[fieldForObjectInstanceToken]?.let {
                        val field = bir2IrConverter.remapElement<IrField>(it)
                        field.parent = bir2IrConverter.remapElement(it[fieldForObjectInstanceParentToken]!!)
                        context.cachedDeclarations.fieldsForObjectInstances.singletonFieldDeclarations[new] = field
                    }
                    old[interfaceCompanionFieldDeclaration]?.let {
                        val field = bir2IrConverter.remapElement<IrField>(it)
                        field.parent = bir2IrConverter.remapElement(it[fieldForObjectInstanceParentToken]!!)
                        context.cachedDeclarations.fieldsForObjectInstances.interfaceCompanionFieldDeclarations[new] = field
                    }
                }
            }

            val newIrModule: IrModuleFragment
            invokePhaseMeasuringTime("BIR", "convert IR to BIR") {
                newIrModule = bir2IrConverter.remapElement<IrModuleFragment>(input.birModule)
            }

            return newIrModule
        }

        override fun outputIfNotEnabled(
            phaseConfig: PhaseConfigurationService,
            phaserState: PhaserState<BirCompilationBundle>,
            context: JvmBackendContext,
            input: BirCompilationBundle,
        ): IrModuleFragment {
            val orgIrModule = input.irModuleFragment
            return if (StopCompilationAfterBir)
                IrModuleFragmentImpl(orgIrModule.descriptor, orgIrModule.irBuiltins)
            else orgIrModule
        }
    }

    @OptIn(ExperimentalContracts::class)
    private fun <R> invokePhaseMeasuringTime(kind: String?, name: String, block: () -> R): R {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        val (result, time) = measureTimedValue(block)
        if (showResultTable) {
            when (kind) {
                "IR" -> irPhasesTime[name] = time
                "BIR" -> birPhasesTime[name] = time
            }
        } else {
            printCompilationPhaseTime(invokedBirCompilation, kind, name, time)
        }
        return result
    }
}