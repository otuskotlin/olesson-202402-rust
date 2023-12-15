import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin("multiplatform") version "1.9.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(17)
    linuxX64 {
        compilations.getByName("main").cinterops {
            create("rust") {
                includeDirs {
                    allHeaders(layout.projectDirectory.dir("rust-lib/target/includes"))
                }
            }
        }
    }
    sourceSets {
        linuxMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.1")
            }
        }

        linuxTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0-RC")
            }
        }
    }
}

tasks {
    // Указываем приложению где искать разделяемые библиотеки Раста
    val linuxX64Test by getting(KotlinNativeTest::class) {
        environment("LD_LIBRARY_PATH", layout.projectDirectory.dir("rust-lib/target/debug").asFile.toString())
    }

    // Ббилдим библиотеки Раста
    val rustBuild by creating(Exec::class) {
        workingDir(layout.projectDirectory.dir("rust-lib"))
        commandLine = listOf(
            "cargo",
            "build"
        )
    }

    // Зацепляем сборку Раста за cinterop
    getByName("cinteropRustLinuxX64") {
        dependsOn(rustBuild)
    }

    clean {
        delete.add(layout.projectDirectory.dir("rust-lib/target"))
    }
}
