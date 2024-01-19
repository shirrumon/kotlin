plugins {
    id("my-plugin")
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    js {
        tasks.register("checkConfigurationsResolve") {
            doLast {
                configurations.named(compilations["main"].npmAggregatedConfigurationName).get().resolve()
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(npm("async", "3.2.4"))
            }
        }
    }
}