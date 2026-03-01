plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

