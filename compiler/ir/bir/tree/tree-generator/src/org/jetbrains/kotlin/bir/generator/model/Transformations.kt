/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.bir.generator.model

import org.jetbrains.kotlin.bir.generator.BirTree
import org.jetbrains.kotlin.bir.generator.config.*
import org.jetbrains.kotlin.bir.generator.elementBaseType
import org.jetbrains.kotlin.bir.generator.util.depthFirstSearch
import org.jetbrains.kotlin.generators.tree.*
import org.jetbrains.kotlin.utils.addToStdlib.UnsafeCastFunction
import org.jetbrains.kotlin.utils.addToStdlib.castAll
import org.jetbrains.kotlin.utils.addToStdlib.partitionIsInstance

private object InferredOverriddenType : TypeRef {
    override val type: String
        get() = error("not supported")
    override val packageName: String?
        get() = null

    override fun getTypeWithArguments(notNull: Boolean): String {
        error("not supported")
    }
}

data class Model(val elements: List<Element>, val rootElement: Element)

fun config2model(config: Config): Model {
    val ec2el = mutableMapOf<ElementConfig, Element>()

    val elements = config.elements.map { ec ->
        Element(
            config = ec,
            name = ec.name,
            packageName = ec.category.packageName,
            params = ec.params,
            fields = ec.fields.mapTo(mutableListOf(), ::transformFieldConfig),
        ).also {
            ec2el[ec.element] = it
        }
    }

    val rootElement = replaceElementRefs(config, ec2el)
    configureInterfacesAndAbstractClasses(elements)
    addAbstractElement(elements)
    adjustSymbolOwners(elements)
    markLeaves(elements)
    configureDescriptorApiAnnotation(elements)
    processFieldOverrides(elements)
    computeAllFields(elements)

    return Model(elements, rootElement)
}

private fun transformFieldConfig(fc: FieldConfig): Field = when (fc) {
    is SimpleFieldConfig -> SingleField(
        fc,
        fc.name,
        fc.type ?: InferredOverriddenType,
        fc.nullable,
        fc.mutable,
        fc.isChild,
        fc.baseDefaultValue,
    )
    is ListFieldConfig -> {
        val listType = when (fc.mutability) {
            ListFieldConfig.Mutability.List -> type(
                "kotlin.collections",
                "MutableList",
            )
            ListFieldConfig.Mutability.Array -> type(
                "kotlin.",
                "Array",
            )
            else -> type("kotlin.collections", "List")
        }
        ListField(
            fc,
            fc.name,
            fc.elementType ?: InferredOverriddenType,
            listType,
            fc.nullable,
            fc.mutability == ListFieldConfig.Mutability.Var,
            fc.isChild,
            fc.mutability != ListFieldConfig.Mutability.Immutable,
            fc.baseDefaultValue,
        )
    }
}

@OptIn(UnsafeCastFunction::class)
private fun replaceElementRefs(config: Config, mapping: Map<ElementConfig, Element>): Element {
    val visited = mutableMapOf<TypeRef, TypeRef>()

    fun transform(type: TypeRef): TypeRef {
        visited[type]?.let {
            return it
        }

        return when (type) {
            is ElementConfigOrRef -> {
                val args = type.args.mapValues { transform(it.value) }
                val el = mapping.getValue(type.element)
                ElementRef(el, args, type.nullable)
            }
            is ClassRef<*> -> {
                @Suppress("UNCHECKED_CAST") // this is the upper bound, compiler could know that, right?
                type as ClassRef<TypeParameterRef>

                val args = type.args.mapValues { transform(it.value) }
                type.copy(args = args)
            }
            else -> type
        }.also { visited[type] = it }
    }

    val rootEl = transform(config.rootElement) as ElementRef

    for (ec in config.elements) {
        val el = mapping[ec.element]!!
        val (elParents, otherParents) = ec.parents
            .map { transform(it) }
            .partitionIsInstance<TypeRef, ElementRef>()
        el.elementParents = elParents.takeIf { it.isNotEmpty() || el == rootEl.element } ?: listOf(rootEl)
        el.otherParents = otherParents.castAll<ClassRef<*>>().toList()

        for (field in el.fields) {
            when (field) {
                is SingleField -> {
                    field.type = transform(field.type)
                }
                is ListField -> {
                    field.elementType = transform(field.elementType)
                }
            }
        }
    }

    return rootEl.element
}

private fun markLeaves(elements: List<Element>) {
    val leaves = elements.toMutableSet()

    for (el in elements) {
        for (parent in el.elementParents) {
            if (!parent.element.isLeaf) {
                leaves.remove(parent.element)
            }
        }
    }

    for (el in leaves) {
        el.isLeaf = true
    }
}

private fun adjustSymbolOwners(elements: List<Element>) {
    for (el in elements) {
        if (depthFirstSearch(ElementRef(el)) { it.element.elementParents }.any { it.element.name == BirTree.symbolOwner.name }) {
            val symbolField = el.fields.singleOrNull { it.name == "symbol" } as SingleField?
            if (symbolField != null) {
                el.fields.remove(symbolField)

                val symbolType = when (val type = symbolField.type) {
                    is ClassRef<*> -> type
                    is TypeVariable -> type.bounds.single() as ClassRef<*>
                    else -> error(type)
                }
                el.otherParents += symbolType
            }
        }
    }
}

private fun addAbstractElement(elements: List<Element>) {
    for (el in elements) {
        if (el.kind!!.typeKind == TypeKind.Class && el.elementParents.none { it.element.kind!!.typeKind == TypeKind.Class }) {
            el.otherParents += elementBaseType
        }
    }
}

private fun configureDescriptorApiAnnotation(elements: List<Element>) {
    for (el in elements) {
        for (field in el.fields) {
            val type = field.type
            if (type is ClassRef<*> && type.packageName.startsWith("org.jetbrains.kotlin.descriptors") &&
                type.simpleName.endsWith("Descriptor") && type.simpleName != "ModuleDescriptor"
            ) {
                field.needsDescriptorApiAnnotation = true
            }
        }
    }
}

private fun processFieldOverrides(elements: List<Element>) {
    for (element in iterateElementsParentFirst(elements)) {
        for (field in element.fields) {
            fun visitParents(visited: Element) {
                for (parent in visited.elementParents) {
                    val overriddenField = parent.element.fields.singleOrNull { it.name == field.name }
                    if (overriddenField != null) {
                        field.isOverride = true
                        field.needsDescriptorApiAnnotation =
                            field.needsDescriptorApiAnnotation || overriddenField.needsDescriptorApiAnnotation

                        fun transformInferredType(type: TypeRef, overriddenType: TypeRef) =
                            type.takeUnless { it is InferredOverriddenType } ?: overriddenType
                        when (field) {
                            is SingleField -> {
                                field.type = transformInferredType(field.type, (overriddenField as SingleField).type)
                            }
                            is ListField -> {
                                field.elementType = transformInferredType(field.elementType, (overriddenField as ListField).elementType)
                            }
                        }

                        break
                    }

                    visitParents(parent.element)
                }
            }

            visitParents(element)
        }
    }
}


private fun computeAllFields(elements: List<Element>) {
    for (element in elements) {
        val allFieldsMap = mutableMapOf<String, Field>()
        val visitedParents = hashSetOf<Element>()
        fun visitParents(visited: Element) {
            visited.elementParents.forEach { parent ->
                if (visitedParents.add(parent.element)) {
                    visitParents(parent.element)
                }
            }

            visited.fields.forEach { field ->
                allFieldsMap[field.name] = field
            }
        }

        visitParents(element)
        val allFields = allFieldsMap.values.toList()

        element.allFields = allFields

        for (field in allFields) {
            field.passViaConstructorParameter = !(field is ListField && field.isChild && field.listType == type("kotlin.collections", "List")) && !field.initializeToThis
        }
    }
}

private fun reorderIfNecessary(fields: List<Field>, order: List<String>?): List<Field> =
    if (order == null) fields else fields.sortedBy {
        val position = order.indexOf(it.name)
        if (position < 0) order.size else position
    }

private fun iterateElementsParentFirst(elements: List<Element>) = sequence {
    val pending = elements.sortedBy { it.elementParents.size }.toMutableSet()
    pendingLoop@ while (pending.isNotEmpty()) {
        val iter = pending.iterator()
        while (iter.hasNext()) {
            val element = iter.next()
            if (element.elementParents.none { it.element in pending }) {
                yield(element)
                iter.remove()
                continue@pendingLoop
            }
        }

        error("Cannot find next element to process")
    }
}

