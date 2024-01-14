/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.logging.Logger
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtensionConfig
import java.io.File

/**
 * Represents a DSL for managing dependencies for Kotlin entities implementing [HasKotlinDependencies].
 */
interface KotlinDependencyHandler : HasProject {

    /**
     * Add an API dependency to this entity (see also [HasKotlinDependencies.apiConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun api(dependencyNotation: Any): Dependency?

    /**
     * Add an API dependency to this entity (see also [HasKotlinDependencies.apiConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun api(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency

    /**
     * Add an API dependency to this entity (see also [HasKotlinDependencies.apiConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun api(dependencyNotation: String, configure: Action<ExternalModuleDependency>): ExternalModuleDependency = api(dependencyNotation) {
        configure.execute(this)
    }

    /**
     * Add an API dependency to this entity (see also [HasKotlinDependencies.apiConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> api(dependency: T, configure: T.() -> Unit): T

    /**
     * Add an API dependency to this entity (see also [HasKotlinDependencies.apiConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> api(dependency: T, configure: Action<T>) = api(dependency) { configure.execute(this) }

    /**
     * Add an implementation dependency to this entity (see also [HasKotlinDependencies.implementationConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun implementation(dependencyNotation: Any): Dependency?

    /**
     * Add an implementation dependency to this entity (see also [HasKotlinDependencies.implementationConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun implementation(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency

    /**
     * Add an implementation dependency to this entity (see also [HasKotlinDependencies.implementationConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun implementation(dependencyNotation: String, configure: Action<ExternalModuleDependency>) =
        implementation(dependencyNotation) { configure.execute(this) }

    /**
     * Add an implementation dependency to this entity (see also [HasKotlinDependencies.implementationConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> implementation(dependency: T, configure: T.() -> Unit): T

    /**
     * Add an implementation dependency to this entity (see also [HasKotlinDependencies.implementationConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> implementation(dependency: T, configure: Action<T>) =
        implementation(dependency) { configure.execute(this) }

    /**
     * Add a compile-only dependency to this entity (see also [HasKotlinDependencies.compileOnlyConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun compileOnly(dependencyNotation: Any): Dependency?

    /**
     * Add a compile-only dependency to this entity (see also [HasKotlinDependencies.compileOnlyConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun compileOnly(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency

    /**
     * Add a compile-only dependency to this entity (see also [HasKotlinDependencies.compileOnlyConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun compileOnly(dependencyNotation: String, configure: Action<ExternalModuleDependency>) =
        compileOnly(dependencyNotation) { configure.execute(this) }

    /**
     * Add a compile-only dependency to this entity (see also [HasKotlinDependencies.compileOnlyConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> compileOnly(dependency: T, configure: T.() -> Unit): T

    /**
     * Add a compile-only dependency to this entity (see also [HasKotlinDependencies.compileOnlyConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> compileOnly(dependency: T, configure: Action<T>) =
        compileOnly(dependency) { configure.execute(this) }

    /**
     * Add a runtime-only dependency to this entity (see also [HasKotlinDependencies.runtimeOnlyConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun runtimeOnly(dependencyNotation: Any): Dependency?

    /**
     * Add a runtime-only dependency to this entity (see also [HasKotlinDependencies.runtimeOnlyConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun runtimeOnly(dependencyNotation: String, configure: ExternalModuleDependency.() -> Unit): ExternalModuleDependency

    /**
     * Add a runtime-only dependency to this entity (see also [HasKotlinDependencies.runtimeOnlyConfigurationName]).
     *
     * @param dependencyNotation The dependency notation, as per [DependencyHandler.create].
     * @param configure additional configuration for the created dependency.
     * @return The dependency, or `null` if dependencyNotation is a provider.
     */
    fun runtimeOnly(dependencyNotation: String, configure: Action<ExternalModuleDependency>) =
        runtimeOnly(dependencyNotation) { configure.execute(this) }

    /**
     * Add a runtime-only dependency to this entity (see also [HasKotlinDependencies.runtimeOnlyConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> runtimeOnly(dependency: T, configure: T.() -> Unit): T

    /**
     * Add a runtime-only dependency to this entity (see also [HasKotlinDependencies.runtimeOnlyConfigurationName]).
     *
     * @param dependency The dependency to add.
     * @param configure additional configuration for the [dependency].
     * @return The added [dependency].
     */
    fun <T : Dependency> runtimeOnly(dependency: T, configure: Action<T>) =
        runtimeOnly(dependency) { configure.execute(this) }

    /**
     * Create an official Kotlin dependency to this entity with the same version as configured
     * in [KotlinTopLevelExtensionConfig.coreLibrariesVersion].
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * The official Kotlin dependencies always have "org.jetbrains.kotlin" group and module name has "kotlin-" prefix.
     *
     * @param simpleModuleName Kotlin module name followed after "kotlin-" prefix. For example, "stdlib" or "test".
     */
    fun kotlin(simpleModuleName: String): ExternalModuleDependency = kotlin(simpleModuleName, null)

    /**
     * Create an official Kotlin dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * The official Kotlin dependencies always have "org.jetbrains.kotlin" group and module name has "kotlin-" prefix.
     *
     * @param simpleModuleName Kotlin module name followed after "kotlin-" prefix. For example, "stdlib" or "test".
     * @param version dependency version or `null` to use the version defined in [KotlinTopLevelExtensionConfig.coreLibrariesVersion].
     */
    fun kotlin(simpleModuleName: String, version: String?): ExternalModuleDependency

    /**
     * Create Gradle project dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * @param path project path
     * @param configuration optional target configuration in the project
     */
    fun project(path: String, configuration: String? = null): ProjectDependency =
        project(listOf("path", "configuration").zip(listOfNotNull(path, configuration)).toMap())

    /**
     * Create Gradle project dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * @param notation project notation described in [DependencyHandler].
     * For example:
     * ```
     * project(mapOf("path" to ":project-a", "configuration" to "someOtherConfiguration"))
     * ```
     */
    fun project(notation: Map<String, Any?>): ProjectDependency

    /**
     * @suppress
     */
    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.enforcedPlatform(notation)")
    )
    fun enforcedPlatform(notation: Any): Dependency =
        project.dependencies.enforcedPlatform(notation)

    /**
     * @suppress
     */
    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.enforcedPlatform(notation, configureAction)")
    )
    fun enforcedPlatform(notation: Any, configureAction: Action<in Dependency>): Dependency =
        project.dependencies.enforcedPlatform(notation, configureAction)

    /**
     * @suppress
     */
    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.platform(notation)")
    )
    fun platform(notation: Any): Dependency =
        project.dependencies.platform(notation)

    /**
     * @suppress
     */
    @Deprecated(
        "Scheduled for removal in Kotlin 2.0. Check KT-58759",
        replaceWith = ReplaceWith("project.dependencies.platform(notation, configureAction)")
    )
    fun platform(notation: Any, configureAction: Action<in Dependency>): Dependency =
        project.dependencies.platform(notation, configureAction)

    /**
     * @suppress
     */
    @Deprecated("Dukat integration is in redesigning process. Now it does not work.")
    fun npm(
        name: String,
        version: String,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return npm(name, version)
    }

    /**
     * Create an [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#dependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param version npm dependency version
     */
    fun npm(
        name: String,
        version: String
    ): Dependency

    /**
     * @suppress
     */
    fun npm(
        name: String,
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return npm(name, directory)
    }

    /**
     * Create an [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#dependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param directory path where dependency files are located
     * (see NPM [directory](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#repository) keyword)
     */
    fun npm(
        name: String,
        directory: File
    ): Dependency

    /**
     * @suppress
     */
    fun npm(
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return npm(directory)
    }

    /**
     * Create an [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#dependencies) dependency.
     * The name of the dependency is derived either from `package.json` file located in the [directory] or [directory] name itself.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param directory path where dependency files are located
     * (see NPM [directory](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#repository) keyword)
     */
    fun npm(
        directory: File
    ): Dependency

    /**
     * Create a dev [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#devdependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param version npm dependency version
     */
    fun devNpm(
        name: String,
        version: String
    ): Dependency

    /**
     * Create a dev [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#devdependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param directory path where dependency files are located
     * (see NPM [directory](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#repository) keyword)
     */
    fun devNpm(
        name: String,
        directory: File
    ): Dependency

    /**
     * Create a dev [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#devdependencies) dependency.
     * The name of the dependency is derived either from `package.json` file located in the [directory] or [directory] name itself.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param directory path where dependency files are located
     * (see NPM [directory](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#repository) keyword)
     */
    fun devNpm(
        directory: File
    ): Dependency

    /**
     * @suppress
     */
    fun optionalNpm(
        name: String,
        version: String,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return optionalNpm(name, version)
    }

    /**
     * Create an optional [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#optionaldependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param version npm dependency version
     */
    fun optionalNpm(
        name: String,
        version: String
    ): Dependency

    /**
     * @suppress
     */
    fun optionalNpm(
        name: String,
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return optionalNpm(name, directory)
    }

    /**
     * Create an optional [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#optionaldependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param directory path where dependency files are located
     * (see NPM [directory](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#repository) keyword)
     */
    fun optionalNpm(
        name: String,
        directory: File
    ): Dependency

    /**
     * @suppress
     */
    fun optionalNpm(
        directory: File,
        generateExternals: Boolean
    ): Dependency {
        @Suppress("deprecation_error")
        warnNpmGenerateExternals(project.logger)
        return optionalNpm(directory)
    }

    /**
     * Create an optional [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#optionaldependencies) dependency.
     * The name of the dependency is derived either from `package.json` file located in the [directory] or [directory] name itself.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param directory path where dependency files are located
     * (see NPM [directory](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#repository) keyword)
     */
    fun optionalNpm(
        directory: File
    ): Dependency

    /**
     * Create a peer [NPM](https://docs.npmjs.com/cli/v10/configuring-npm/package-json#peerdependencies) dependency.
     * Created dependency should be manually added to this entity using other methods from this DSL!
     *
     * **Note**: Only relevant for Kotlin entities targeting only [KotlinPlatformType.js] or [KotlinPlatformType.wasm]!
     *
     * @param name npm dependency name
     * @param version npm dependency version
     */
    fun peerNpm(
        name: String,
        version: String
    ): Dependency
}

/**
 * Represents a Kotlin DSL entity having configurable Kotlin dependencies.
 */
interface HasKotlinDependencies {

    /**
     * Configure dependencies for this entity.
     */
    fun dependencies(configure: KotlinDependencyHandler.() -> Unit)

    /**
     * Configure dependencies for this entity.
     */
    fun dependencies(configure: Action<KotlinDependencyHandler>)

    /**
     * The name of the API Gradle configuration for this entity.
     *
     * The API configuration contains dependencies which are exported by this entity, and is not transitive by default.
     *
     * This configuration is not meant to be resolved.
     */
    val apiConfigurationName: String

    /**
     * The name of the implementation Gradle configuration for this entity.
     *
     * The implementation configuration should contain dependencies which are specific to the implementation of the component
     * (internal APIs).
     *
     * This configuration is not meant to be resolved.
     */
    val implementationConfigurationName: String

    /**
     * The name of the compile-only Gradle configuration for this entity.
     *
     * The compile-only configuration contains dependencies which are participating in compilation,
     * but should be added explicitly in the runtime.
     *
     * This configuration is not meant to be resolved.
     */
    val compileOnlyConfigurationName: String

    /**
     * The name of the runtime-only Gradle configuration for this entity.
     *
     * The runtime-only configuration contains dependencies which are not participating in the compilation,
     * but added in the runtime.
     *
     * This configuration is not meant to be resolved.
     */
    val runtimeOnlyConfigurationName: String
}

/**
 * @suppress
 */
@Deprecated(
    message = "Do not use in your build script",
    level = DeprecationLevel.ERROR
)
fun warnNpmGenerateExternals(logger: Logger) {
    logger.warn(
        """
        |
        |==========
        |Please note, Dukat integration in Gradle plugin does not work now.
        |It is in redesigning process.
        |==========
        |
        """.trimMargin()
    )
}
