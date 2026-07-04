plugins {
    `java-library`
    `maven-publish`
}

group = "io.github.danlewis783"
version = "3.0.14-SNAPSHOT"

val junitVersion = "5.14.4"
val assertjVersion = "3.27.7"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter(junitVersion)
            dependencies {
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }

        register<JvmTestSuite>("userInterfaceTest") {
            useJUnitJupiter(junitVersion)
            dependencies {
                implementation(project())
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
            targets {
                all {
                    testTask.configure {
                        // one JVM per test class, mirroring the jtreg othervm isolation
                        // these tests relied on (UIManager L&F and JemmyProperties are global)
                        maxParallelForks = 1
                        forkEvery = 1
                        shouldRunAfter(tasks.test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("userInterfaceTest"))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.processResources {
    val buildNumber = version.toString()
    filesMatching("org/netbeans/jemmy/version_info") {
        filter(mapOf("tokens" to mapOf("BUILD_NUMBER" to buildNumber)),
            org.apache.tools.ant.filters.ReplaceTokens::class.java)
    }
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.netbeans.jemmy.JemmyProperties",
        )
    }
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        addStringOption("Xdoclint:none", "-quiet")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name = "Jemmy"
                description = "Jemmy - a UI automation library for AWT/Swing applications (fork of openjdk/jemmy-v2)"
                url = "https://github.com/openjdk/jemmy-v2"
                licenses {
                    license {
                        name = "GNU General Public License, version 2, with the Classpath Exception"
                        url = "https://openjdk.org/legal/gplv2+ce.html"
                    }
                }
            }
        }
    }
}
