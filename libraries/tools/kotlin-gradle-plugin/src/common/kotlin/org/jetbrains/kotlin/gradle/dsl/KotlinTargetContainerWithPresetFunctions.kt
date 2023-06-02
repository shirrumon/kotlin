package org.jetbrains.kotlin.gradle.dsl

import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetsContainerWithPresets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTestsPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTestsPreset
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.konan.target.DEPRECATED_TARGET_MESSAGE

// DO NOT EDIT MANUALLY! Generated by org.jetbrains.kotlin.generators.gradle.dsl.MppPresetFunctionsCodegenKt

private const val ANDROID_TARGET_MIGRATION_MESSAGE = "Please use androidTarget() instead. Learn more here: https://kotl.in/android-target-dsl"

interface KotlinTargetContainerWithPresetFunctions : KotlinTargetsContainerWithPresets {

    fun jvm(
        name: String = "jvm",
        configure: KotlinJvmTarget.() -> Unit = { }
    ): KotlinJvmTarget =
        configureOrCreate(
            name,
            presets.getByName("jvm") as KotlinJvmTargetPreset,
            configure
        )

    fun jvm() = jvm("jvm") { }
    fun jvm(name: String) = jvm(name) { }
    fun jvm(name: String, configure: Action<KotlinJvmTarget>) = jvm(name) { configure.execute(this) }
    fun jvm(configure: Action<KotlinJvmTarget>) = jvm { configure.execute(this) }

    fun androidTarget(
        name: String = "android",
        configure: KotlinAndroidTarget.() -> Unit = { }
    ): KotlinAndroidTarget =
        configureOrCreate(
            name,
            presets.getByName("android") as KotlinAndroidTargetPreset,
            configure
        )

    fun androidTarget() = androidTarget("android") { }
    fun androidTarget(name: String) = androidTarget(name) { }
    fun androidTarget(name: String, configure: Action<KotlinAndroidTarget>) = androidTarget(name) { configure.execute(this) }
    fun androidTarget(configure: Action<KotlinAndroidTarget>) = androidTarget { configure.execute(this) }


    @Deprecated(ANDROID_TARGET_MIGRATION_MESSAGE)
    fun android(
        name: String = "android",
        configure: KotlinAndroidTarget.() -> Unit = { }
    ): KotlinAndroidTarget =
        configureOrCreate(
            name,
            presets.getByName("android") as KotlinAndroidTargetPreset,
            configure
        ).also {
            it.project.logger.warn(
                """
                    w: Please use `androidTarget` function instead of `android` to configure android target inside `kotlin { }` block.
                    See the details here: https://kotl.in/android-target-dsl
                """.trimIndent()
            )
        }


    @Deprecated(ANDROID_TARGET_MIGRATION_MESSAGE, replaceWith = ReplaceWith("androidTarget()"))
    @Suppress("DEPRECATION")
    fun android() = android("android") { }

    @Deprecated(ANDROID_TARGET_MIGRATION_MESSAGE, replaceWith = ReplaceWith("androidTarget(name)"))
    @Suppress("DEPRECATION")
    fun android(name: String) = android(name) { }

    @Deprecated(ANDROID_TARGET_MIGRATION_MESSAGE)
    @Suppress("DEPRECATION")
    fun android(name: String, configure: Action<KotlinAndroidTarget>) = android(name) { configure.execute(this) }

    @Deprecated(ANDROID_TARGET_MIGRATION_MESSAGE)
    @Suppress("DEPRECATION")
    fun android(configure: Action<KotlinAndroidTarget>) = android { configure.execute(this) }

    fun androidNativeX64(
        name: String = "androidNativeX64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("androidNativeX64") as KotlinNativeTargetPreset,
            configure
        )

    fun androidNativeX64() = androidNativeX64("androidNativeX64") { }
    fun androidNativeX64(name: String) = androidNativeX64(name) { }
    fun androidNativeX64(name: String, configure: Action<KotlinNativeTarget>) = androidNativeX64(name) { configure.execute(this) }
    fun androidNativeX64(configure: Action<KotlinNativeTarget>) = androidNativeX64 { configure.execute(this) }

    fun androidNativeX86(
        name: String = "androidNativeX86",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("androidNativeX86") as KotlinNativeTargetPreset,
            configure
        )

    fun androidNativeX86() = androidNativeX86("androidNativeX86") { }
    fun androidNativeX86(name: String) = androidNativeX86(name) { }
    fun androidNativeX86(name: String, configure: Action<KotlinNativeTarget>) = androidNativeX86(name) { configure.execute(this) }
    fun androidNativeX86(configure: Action<KotlinNativeTarget>) = androidNativeX86 { configure.execute(this) }

    fun androidNativeArm32(
        name: String = "androidNativeArm32",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("androidNativeArm32") as KotlinNativeTargetPreset,
            configure
        )

    fun androidNativeArm32() = androidNativeArm32("androidNativeArm32") { }
    fun androidNativeArm32(name: String) = androidNativeArm32(name) { }
    fun androidNativeArm32(name: String, configure: Action<KotlinNativeTarget>) = androidNativeArm32(name) { configure.execute(this) }
    fun androidNativeArm32(configure: Action<KotlinNativeTarget>) = androidNativeArm32 { configure.execute(this) }

    fun androidNativeArm64(
        name: String = "androidNativeArm64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("androidNativeArm64") as KotlinNativeTargetPreset,
            configure
        )

    fun androidNativeArm64() = androidNativeArm64("androidNativeArm64") { }
    fun androidNativeArm64(name: String) = androidNativeArm64(name) { }
    fun androidNativeArm64(name: String, configure: Action<KotlinNativeTarget>) = androidNativeArm64(name) { configure.execute(this) }
    fun androidNativeArm64(configure: Action<KotlinNativeTarget>) = androidNativeArm64 { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun iosArm32(
        name: String = "iosArm32",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("iosArm32") as KotlinNativeTargetPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun iosArm32() = iosArm32("iosArm32") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun iosArm32(name: String) = iosArm32(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun iosArm32(name: String, configure: Action<KotlinNativeTarget>) = iosArm32(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun iosArm32(configure: Action<KotlinNativeTarget>) = iosArm32 { configure.execute(this) }

    fun iosArm64(
        name: String = "iosArm64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("iosArm64") as KotlinNativeTargetPreset,
            configure
        )

    fun iosArm64() = iosArm64("iosArm64") { }
    fun iosArm64(name: String) = iosArm64(name) { }
    fun iosArm64(name: String, configure: Action<KotlinNativeTarget>) = iosArm64(name) { configure.execute(this) }
    fun iosArm64(configure: Action<KotlinNativeTarget>) = iosArm64 { configure.execute(this) }

    fun iosX64(
        name: String = "iosX64",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("iosX64") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )

    fun iosX64() = iosX64("iosX64") { }
    fun iosX64(name: String) = iosX64(name) { }
    fun iosX64(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = iosX64(name) { configure.execute(this) }
    fun iosX64(configure: Action<KotlinNativeTargetWithSimulatorTests>) = iosX64 { configure.execute(this) }

    fun iosSimulatorArm64(
        name: String = "iosSimulatorArm64",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("iosSimulatorArm64") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )

    fun iosSimulatorArm64() = iosSimulatorArm64("iosSimulatorArm64") { }
    fun iosSimulatorArm64(name: String) = iosSimulatorArm64(name) { }
    fun iosSimulatorArm64(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = iosSimulatorArm64(name) { configure.execute(this) }
    fun iosSimulatorArm64(configure: Action<KotlinNativeTargetWithSimulatorTests>) = iosSimulatorArm64 { configure.execute(this) }

    fun watchosArm32(
        name: String = "watchosArm32",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("watchosArm32") as KotlinNativeTargetPreset,
            configure
        )

    fun watchosArm32() = watchosArm32("watchosArm32") { }
    fun watchosArm32(name: String) = watchosArm32(name) { }
    fun watchosArm32(name: String, configure: Action<KotlinNativeTarget>) = watchosArm32(name) { configure.execute(this) }
    fun watchosArm32(configure: Action<KotlinNativeTarget>) = watchosArm32 { configure.execute(this) }

    fun watchosArm64(
        name: String = "watchosArm64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("watchosArm64") as KotlinNativeTargetPreset,
            configure
        )

    fun watchosArm64() = watchosArm64("watchosArm64") { }
    fun watchosArm64(name: String) = watchosArm64(name) { }
    fun watchosArm64(name: String, configure: Action<KotlinNativeTarget>) = watchosArm64(name) { configure.execute(this) }
    fun watchosArm64(configure: Action<KotlinNativeTarget>) = watchosArm64 { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun watchosX86(
        name: String = "watchosX86",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("watchosX86") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun watchosX86() = watchosX86("watchosX86") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun watchosX86(name: String) = watchosX86(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun watchosX86(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = watchosX86(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun watchosX86(configure: Action<KotlinNativeTargetWithSimulatorTests>) = watchosX86 { configure.execute(this) }

    fun watchosX64(
        name: String = "watchosX64",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("watchosX64") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )

    fun watchosX64() = watchosX64("watchosX64") { }
    fun watchosX64(name: String) = watchosX64(name) { }
    fun watchosX64(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = watchosX64(name) { configure.execute(this) }
    fun watchosX64(configure: Action<KotlinNativeTargetWithSimulatorTests>) = watchosX64 { configure.execute(this) }

    fun watchosSimulatorArm64(
        name: String = "watchosSimulatorArm64",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("watchosSimulatorArm64") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )

    fun watchosSimulatorArm64() = watchosSimulatorArm64("watchosSimulatorArm64") { }
    fun watchosSimulatorArm64(name: String) = watchosSimulatorArm64(name) { }
    fun watchosSimulatorArm64(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = watchosSimulatorArm64(name) { configure.execute(this) }
    fun watchosSimulatorArm64(configure: Action<KotlinNativeTargetWithSimulatorTests>) = watchosSimulatorArm64 { configure.execute(this) }

    fun watchosDeviceArm64(
        name: String = "watchosDeviceArm64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("watchosDeviceArm64") as KotlinNativeTargetPreset,
            configure
        )

    fun watchosDeviceArm64() = watchosDeviceArm64("watchosDeviceArm64") { }
    fun watchosDeviceArm64(name: String) = watchosDeviceArm64(name) { }
    fun watchosDeviceArm64(name: String, configure: Action<KotlinNativeTarget>) = watchosDeviceArm64(name) { configure.execute(this) }
    fun watchosDeviceArm64(configure: Action<KotlinNativeTarget>) = watchosDeviceArm64 { configure.execute(this) }

    fun tvosArm64(
        name: String = "tvosArm64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("tvosArm64") as KotlinNativeTargetPreset,
            configure
        )

    fun tvosArm64() = tvosArm64("tvosArm64") { }
    fun tvosArm64(name: String) = tvosArm64(name) { }
    fun tvosArm64(name: String, configure: Action<KotlinNativeTarget>) = tvosArm64(name) { configure.execute(this) }
    fun tvosArm64(configure: Action<KotlinNativeTarget>) = tvosArm64 { configure.execute(this) }

    fun tvosX64(
        name: String = "tvosX64",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("tvosX64") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )

    fun tvosX64() = tvosX64("tvosX64") { }
    fun tvosX64(name: String) = tvosX64(name) { }
    fun tvosX64(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = tvosX64(name) { configure.execute(this) }
    fun tvosX64(configure: Action<KotlinNativeTargetWithSimulatorTests>) = tvosX64 { configure.execute(this) }

    fun tvosSimulatorArm64(
        name: String = "tvosSimulatorArm64",
        configure: KotlinNativeTargetWithSimulatorTests.() -> Unit = { }
    ): KotlinNativeTargetWithSimulatorTests =
        configureOrCreate(
            name,
            presets.getByName("tvosSimulatorArm64") as KotlinNativeTargetWithSimulatorTestsPreset,
            configure
        )

    fun tvosSimulatorArm64() = tvosSimulatorArm64("tvosSimulatorArm64") { }
    fun tvosSimulatorArm64(name: String) = tvosSimulatorArm64(name) { }
    fun tvosSimulatorArm64(name: String, configure: Action<KotlinNativeTargetWithSimulatorTests>) = tvosSimulatorArm64(name) { configure.execute(this) }
    fun tvosSimulatorArm64(configure: Action<KotlinNativeTargetWithSimulatorTests>) = tvosSimulatorArm64 { configure.execute(this) }

    fun linuxX64(
        name: String = "linuxX64",
        configure: KotlinNativeTargetWithHostTests.() -> Unit = { }
    ): KotlinNativeTargetWithHostTests =
        configureOrCreate(
            name,
            presets.getByName("linuxX64") as KotlinNativeTargetWithHostTestsPreset,
            configure
        )

    fun linuxX64() = linuxX64("linuxX64") { }
    fun linuxX64(name: String) = linuxX64(name) { }
    fun linuxX64(name: String, configure: Action<KotlinNativeTargetWithHostTests>) = linuxX64(name) { configure.execute(this) }
    fun linuxX64(configure: Action<KotlinNativeTargetWithHostTests>) = linuxX64 { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun mingwX86(
        name: String = "mingwX86",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("mingwX86") as KotlinNativeTargetPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun mingwX86() = mingwX86("mingwX86") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun mingwX86(name: String) = mingwX86(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun mingwX86(name: String, configure: Action<KotlinNativeTarget>) = mingwX86(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun mingwX86(configure: Action<KotlinNativeTarget>) = mingwX86 { configure.execute(this) }

    fun mingwX64(
        name: String = "mingwX64",
        configure: KotlinNativeTargetWithHostTests.() -> Unit = { }
    ): KotlinNativeTargetWithHostTests =
        configureOrCreate(
            name,
            presets.getByName("mingwX64") as KotlinNativeTargetWithHostTestsPreset,
            configure
        )

    fun mingwX64() = mingwX64("mingwX64") { }
    fun mingwX64(name: String) = mingwX64(name) { }
    fun mingwX64(name: String, configure: Action<KotlinNativeTargetWithHostTests>) = mingwX64(name) { configure.execute(this) }
    fun mingwX64(configure: Action<KotlinNativeTargetWithHostTests>) = mingwX64 { configure.execute(this) }

    fun macosX64(
        name: String = "macosX64",
        configure: KotlinNativeTargetWithHostTests.() -> Unit = { }
    ): KotlinNativeTargetWithHostTests =
        configureOrCreate(
            name,
            presets.getByName("macosX64") as KotlinNativeTargetWithHostTestsPreset,
            configure
        )

    fun macosX64() = macosX64("macosX64") { }
    fun macosX64(name: String) = macosX64(name) { }
    fun macosX64(name: String, configure: Action<KotlinNativeTargetWithHostTests>) = macosX64(name) { configure.execute(this) }
    fun macosX64(configure: Action<KotlinNativeTargetWithHostTests>) = macosX64 { configure.execute(this) }

    fun macosArm64(
        name: String = "macosArm64",
        configure: KotlinNativeTargetWithHostTests.() -> Unit = { }
    ): KotlinNativeTargetWithHostTests =
        configureOrCreate(
            name,
            presets.getByName("macosArm64") as KotlinNativeTargetWithHostTestsPreset,
            configure
        )

    fun macosArm64() = macosArm64("macosArm64") { }
    fun macosArm64(name: String) = macosArm64(name) { }
    fun macosArm64(name: String, configure: Action<KotlinNativeTargetWithHostTests>) = macosArm64(name) { configure.execute(this) }
    fun macosArm64(configure: Action<KotlinNativeTargetWithHostTests>) = macosArm64 { configure.execute(this) }

    fun linuxArm64(
        name: String = "linuxArm64",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("linuxArm64") as KotlinNativeTargetPreset,
            configure
        )

    fun linuxArm64() = linuxArm64("linuxArm64") { }
    fun linuxArm64(name: String) = linuxArm64(name) { }
    fun linuxArm64(name: String, configure: Action<KotlinNativeTarget>) = linuxArm64(name) { configure.execute(this) }
    fun linuxArm64(configure: Action<KotlinNativeTarget>) = linuxArm64 { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun linuxArm32Hfp(
        name: String = "linuxArm32Hfp",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("linuxArm32Hfp") as KotlinNativeTargetPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxArm32Hfp() = linuxArm32Hfp("linuxArm32Hfp") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxArm32Hfp(name: String) = linuxArm32Hfp(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxArm32Hfp(name: String, configure: Action<KotlinNativeTarget>) = linuxArm32Hfp(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxArm32Hfp(configure: Action<KotlinNativeTarget>) = linuxArm32Hfp { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun linuxMips32(
        name: String = "linuxMips32",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("linuxMips32") as KotlinNativeTargetPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMips32() = linuxMips32("linuxMips32") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMips32(name: String) = linuxMips32(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMips32(name: String, configure: Action<KotlinNativeTarget>) = linuxMips32(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMips32(configure: Action<KotlinNativeTarget>) = linuxMips32 { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun linuxMipsel32(
        name: String = "linuxMipsel32",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("linuxMipsel32") as KotlinNativeTargetPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMipsel32() = linuxMipsel32("linuxMipsel32") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMipsel32(name: String) = linuxMipsel32(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMipsel32(name: String, configure: Action<KotlinNativeTarget>) = linuxMipsel32(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun linuxMipsel32(configure: Action<KotlinNativeTarget>) = linuxMipsel32 { configure.execute(this) }


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    fun wasm32(
        name: String = "wasm32",
        configure: KotlinNativeTarget.() -> Unit = { }
    ): KotlinNativeTarget =
        configureOrCreate(
            name,
            presets.getByName("wasm32") as KotlinNativeTargetPreset,
            configure
        )


    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun wasm32() = wasm32("wasm32") { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun wasm32(name: String) = wasm32(name) { }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun wasm32(name: String, configure: Action<KotlinNativeTarget>) = wasm32(name) { configure.execute(this) }

    @Deprecated(DEPRECATED_TARGET_MESSAGE)
    @Suppress("DEPRECATION")
    fun wasm32(configure: Action<KotlinNativeTarget>) = wasm32 { configure.execute(this) }

}