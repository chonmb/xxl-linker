import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer



plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.chonmb"
version = "1.0.3"

repositories {
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {

    intellijPlatform {
        create("IC", "2025.1.4.1")


        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        // Add necessary plugin dependencies for compilation here, example:
        bundledPlugin("com.intellij.java")

    }
    implementation("com.dtflys.forest:forest-core:1.8.0")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.61")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

buildscript {
    dependencies {
        classpath("org.commonmark:commonmark:0.21.0")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    patchPluginXml {
        pluginDescription.set(provider {
            Parser.builder().build().parse(
                file("$rootDir/README.md")
                    .readText()
                    .replaceFirst(Regex("#.*[\r|\n]*"),"")
                    .replace(Regex("!\\[.*?\\]\\(.*?\\)[\\n|\\r]*"),"")
            ).let {
                HtmlRenderer.builder().build().render(it)
            }
        })
    }
}

tasks.buildPlugin {
    archiveBaseName.set("xxl-linker")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

