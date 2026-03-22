import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    id("jacoco")
    id("signing")
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.jmailen.kotlinter") version "5.4.2"
    id("com.adarshr.test-logger") version "4.0.0"
}

repositories {
    mavenCentral()
}

subprojects {
    val junitVersion = "5.11.4"
    val assertjVersion = "3.27.7"

    apply {
        plugin("kotlin")
        plugin("jacoco")
        plugin("signing")
        plugin("maven-publish")
        plugin("org.jetbrains.dokka")
        plugin("org.jmailen.kotlinter")
        plugin("com.adarshr.test-logger")
    }

    group = "org.valiktor"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))

        testImplementation(kotlin("test-junit5"))
        testImplementation("org.assertj:assertj-core:$assertjVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    testlogger {
        setTheme("mocha")
    }

    tasks {
        compileKotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        compileTestKotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        processResources {
            filteringCharset = "UTF-8"
            filesMatching("**/*.properties") {
                filter(org.apache.tools.ant.filters.EscapeUnicode::class)
            }
        }

        test {
            useJUnitPlatform()

            // fix for JDK > 8 (see http://openjdk.java.net/jeps/252)
            systemProperty("java.locale.providers", "JRE,SPI")
        }

        processTestResources {
            filteringCharset = "UTF-8"
            filesMatching("**/*.properties") {
                filter(org.apache.tools.ant.filters.EscapeUnicode::class)
            }
        }

        named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaJavadoc") {
            outputDirectory.set(layout.buildDirectory.dir("javadoc").get().asFile)
        }

        jacocoTestReport {
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }

        jacocoTestCoverageVerification {
            dependsOn(jacocoTestReport)

            violationRules {
                rule { limit { minimum = 0.3.toBigDecimal() } }
            }
        }

        check {
            dependsOn(jacocoTestCoverageVerification)
        }
    }

    publishing {
        val ossrhUsername: String by project
        val ossrhPassword: String by project

        repositories {
            maven(url = "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                val binaryJar = components["java"]

                val sourcesJar by tasks.registering(Jar::class) {
                    archiveClassifier.set("sources")
                    from(sourceSets["main"].allSource)
                }

                val javadocJar by tasks.registering(Jar::class) {
                    archiveClassifier.set("javadoc")
                    dependsOn(tasks.named("dokkaJavadoc"))
                    from(layout.buildDirectory.dir("javadoc"))
                }

                from(binaryJar)
                artifact(sourcesJar)
                artifact(javadocJar)

                pom {
                    name.set("Valiktor")
                    description.set("Valiktor is a type-safe, powerful and extensible fluent DSL to validate objects in Kotlin.")
                    url.set("https://www.valiktor.org")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("rodolphocouto")
                            name.set("Rodolpho Sbaraglini Couto")
                            email.set("rodolpho.sbaraglini@gmail.com")
                        }
                    }
                    scm {
                        url.set("https://www.github.com/valiktor/valiktor")
                        connection.set("scm:git:https://www.github.com/valiktor/valiktor")
                        developerConnection.set("scm:git:https://www.github.com/rodolphocouto")
                    }
                }
            }
        }
    }

    afterEvaluate {
        signing {
            sign(publishing.publications["mavenJava"])
        }
    }
}
