plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        useCommonJs()
        browser {
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":base"))
            }
        }
    }
}