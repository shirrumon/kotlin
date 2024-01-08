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

        val otherDist by configurations.creating {
            isCanBeConsumed = true
            isCanBeResolved = false
        }
        val runtimeOnly by configurations.getting
        runtimeOnly.extendsFrom(otherDist)
        artifacts {
            add(otherDist.name, tasks.named("otherKlib").map { it.outputs.files.files.first() })
        }
        useCommonJs()
        browser {
        }
    }

    sourceSets {
        val jsMain by getting {
            kotlin.exclude("**/other/**")
        }
        val jsOther by getting {
            kotlin.srcDirs("src/jsMain/kotlin/other")
            dependencies {
                implementation(project(path = project.path))
            }
        }
    }
}