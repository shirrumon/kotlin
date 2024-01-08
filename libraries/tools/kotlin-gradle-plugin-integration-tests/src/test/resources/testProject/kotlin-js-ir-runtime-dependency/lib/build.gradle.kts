plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        val otherCompilation = compilations.create("other")
        tasks.register<Zip>("otherKlib") {
            from(otherCompilation.output.allOutputs)
            archiveExtension.set("klib")
        }

        useCommonJs()
        browser {
        }
    }

    sourceSets {
        val jsMain by getting {
            kotlin.exclude("**/jsOther/**")
            dependencies {
                runtimeOnly(files(tasks.named("otherKlib")))
            }
        }
        val jsOther by getting {
            kotlin.srcDirs("src/jsMain/kotlin/jsOther")
            dependencies {
                implementation(project(path = project.path))
            }
        }
    }
}