import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.IR as IR_TYPE

plugins {
    kotlin("multiplatform")
}

@Suppress("DEPRECATION")
kotlin {
    js("js", "ir")
    js(IR)
    js(IR_TYPE) {
        useCommonJs()
        browser {
        }
    }
}