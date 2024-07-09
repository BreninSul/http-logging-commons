/*
 * MIT License
 *
 * Copyright (c) 2024 BreninSul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



plugins {
    val kotlinVersion = "2.0.0"
    id("java-library")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.dokka") version "1.9.20"
}

val kotlinVersion = "2.0.0"
val javaVersion = JavaVersion.VERSION_17

group = "io.github.breninsul"
version = "1.0.1"

java {
    sourceCompatibility = javaVersion
}
java {
    withJavadocJar()
    withSourcesJar()
}
repositories {
    mavenCentral()
}
tasks.compileJava {
    dependsOn.add(tasks.processResources)
}
tasks.compileKotlin {
    dependsOn.add(tasks.processResources)
}

dependencies {
//    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
//    kapt("org.springframework.boot:spring-boot-configuration-processor")
}
val javadocJar =
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaJavadoc"))
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
tasks.getByName<Jar>("jar") {
    enabled = true
    archiveClassifier = ""
}


kotlin {
    jvmToolchain(javaVersion.majorVersion.toInt())
}



signing {
    useGpgCmd()
}


val repoName = "http-logging-commons"
centralPortal {
    pom {
        packaging = "jar"
        name.set("BreninSul http logging commons part")
        url.set("https://github.com/BreninSul/$repoName")
        description.set("BreninSul http logging commons part")
        licenses {
            license {
                name.set("MIT License")
                url.set("http://opensource.org/licenses/MIT")
            }
        }
        scm {
            connection.set("scm:https://github.com/BreninSul/$repoName.git")
            developerConnection.set("scm:git@github.com:BreninSul/$repoName.git")
            url.set("https://github.com/BreninSul/$repoName")
        }
        developers {
            developer {
                id.set("BreninSul")
                name.set("BreninSul")
                email.set("brenimnsul@gmail.com")
                url.set("breninsul.github.io")
            }
        }
    }
}



tasks.withType<Test> {
    useJUnitPlatform()
}