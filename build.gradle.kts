plugins {
    val kotlinVersion = "1.3.21"
    kotlin("multiplatform") version kotlinVersion
    id("kotlinx-serialization") version kotlinVersion
    idea
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://kotlin.bintray.com/kotlinx")
}

kotlin {
    jvm()
    //js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.1.1")
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.10.0")
                api("org.jetbrains.kotlinx:kotlinx-io:0.1.7")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-io:0.1.7")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.10.0")
                api("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.7")
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        //js().compilations["main"]
    }
}