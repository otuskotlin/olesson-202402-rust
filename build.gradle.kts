import org.gradle.internal.jvm.Jvm
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import java.io.ByteArrayOutputStream

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
    jvm()
    linuxX64 {
        compilations.getByName("main").cinterops {
            create("rust")
        }
    }
    sourceSets {
        jvmMain {}
        jvmTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
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

val rustLibDir = layout.projectDirectory.dir("rust-lib/target/debug").asFile.toString()
tasks {
    // Указываем приложению где искать разделяемые библиотеки Раста
    val linuxX64Test by getting(KotlinNativeTest::class) {
        environment("LD_LIBRARY_PATH", rustLibDir)
    }
    withType(Test::class).all {
        systemProperty("java.library.path", rustLibDir)
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

// Код для генерации h-файла из JNI-класса в Kotlin
val jniHeaderDirectory = layout.buildDirectory.dir("jniIncludes").get().asFile
val generateJniHeaders: Task by tasks.creating {
    group = "build"
    dependsOn(tasks.getByName("compileKotlinJvm"))

    // For caching
    inputs.dir("src/jvmMain/kotlin")
//    outputs.dir("src/jvmMain/generated/jni")
    outputs.dir(jniHeaderDirectory)

    doLast {
        val javaHome = Jvm.current().javaHome
        val javap = javaHome.resolve("bin").walk().firstOrNull { it.name.startsWith("javap") }?.absolutePath ?: error("javap not found")
        val javac = javaHome.resolve("bin").walk().firstOrNull { it.name.startsWith("javac") }?.absolutePath ?: error("javac not found")
        val buildDir = layout.buildDirectory.file("classes/kotlin/jvm/main").get().asFile
        val tmpDir = layout.buildDirectory.file("tmp/jvmJni").get().asFile.apply { mkdirs() }

        val bodyExtractingRegex = """^.+\Rpublic \w* ?class ([^\s]+).*\{\R((?s:.+))\}\R$""".toRegex()
        val nativeMethodExtractingRegex = """.*\bnative\b.*""".toRegex()

        buildDir.walkTopDown()
            .filter { "META" !in it.absolutePath }
            .forEach { file ->
                println("FILE: $file")
                if (!file.isFile) return@forEach

                val output = ByteArrayOutputStream().use {
                    project.exec {
                        commandLine(javap, "-private", "-cp", buildDir.absolutePath, file.absolutePath)
                        standardOutput = it
                    }.assertNormalExitValue()
                    it.toString()
                }

                val (qualifiedName, methodInfo) = bodyExtractingRegex.find(output)?.destructured ?: return@forEach

                val lastDot = qualifiedName.lastIndexOf('.').takeIf { it >= 0 } ?: return@forEach
                val packageName = qualifiedName.substring(0, lastDot)
                val className = qualifiedName.substring(lastDot+1, qualifiedName.length)

                val nativeMethods = nativeMethodExtractingRegex
                    .findAll(methodInfo)
                    .map { it.groups }
                    .flatMap { it.asSequence().mapNotNull { group -> group?.value } }
                    .toList()

                if (nativeMethods.isEmpty()) return@forEach

                val source = buildString {
                    appendLine("package $packageName;")
                    appendLine("public class $className {")
                    for (method in nativeMethods) {
                        if ("()" in method) appendLine(method)
                        else {
                            val updatedMethod = StringBuilder(method).apply {
                                var count = 0
                                var i = 0
                                while (i < length) {
                                    if (this[i] == ',' || this[i] == ')') insert(i, " arg${count++}".also { i += it.length + 1 })
                                    else i++
                                }
                            }
                            appendLine(updatedMethod)
                        }
                    }
                    appendLine("}")
                }
                val outputFile = tmpDir.resolve(packageName.replace(".", "/")).apply { mkdirs() }.resolve("$className.java").apply { delete() }.apply { createNewFile() }
                outputFile.writeText(source)

                project.exec {
                    commandLine(javac, "-h", jniHeaderDirectory.absolutePath, outputFile.absolutePath)
                }.assertNormalExitValue()
            }
    }
}
