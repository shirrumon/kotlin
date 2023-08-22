plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    api(project(":compiler:ir.tree"))
    api(project(":compiler:ir.serialization.common"))
    api(project(":core:descriptors.jvm"))
    api(project(":core:metadata.jvm"))
    implementation(project(":core:deserialization.common.jvm"))
    api(project(":compiler:frontend.java"))
    implementation(project(":compiler:backend.jvm"))
    compileOnly(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }
}

sourceSets {
    "main" {
        projectDefault()
    }
    "test" {}
}
