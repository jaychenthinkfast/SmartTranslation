plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.1"
}

group = "com.github.smarttranslation"
version = "1.0.0"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-dist") }
    mavenCentral()
}

// Configure IntelliJ Platform Plugin
intellij {
    version.set("2023.3")
    type.set("GO") // GoLand
    plugins.set(listOf("org.jetbrains.plugins.go"))
    downloadSources.set(false)
    updateSinceUntilBuild.set(true)
    sameSinceUntilBuild.set(false)
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.json:json:20231013")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks {
    // Set Java compiler version
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    patchPluginXml {
        version.set(project.version.toString())
        sinceBuild.set("233")
        untilBuild.set("243.*")
        
        pluginDescription.set("""
            SmartTranslation is an intelligent translation plugin developed for JetBrains IDEs, supporting multiple translation engines and providing convenient code comment and document translation functions.
            
            Main Features:
            - Multiple translation engines support
              * Google Translate (no API key required)
              * DeepSeek AI Translation (API key required)
            - Convenient translation operations
              * Translate selected text in the editor using keyboard shortcuts
              * Quick access to translation functions from the editor's context menu
              * Custom keyboard shortcut support
            - Personalized settings
              * Customize target and source languages
              * Configure API keys
              * Translation history management
              
            This plugin helps developers quickly translate comments, documentation, and code snippets between different languages, improving development efficiency and international collaboration.
        """.trimIndent())
        
        changeNotes.set("""
            <h3>1.0.0</h3>
            <ul>
                <li>Initial release</li>
                <li>Support for Google Translate (no API key required)</li>
                <li>Support for DeepSeek AI Translation</li>
                <li>Keyboard shortcuts and right-click menu translation</li>
                <li>Custom language settings</li>
                <li>Translation history</li>
            </ul>
        """.trimIndent())
    }

    test {
        useJUnitPlatform()
    }

    buildSearchableOptions {
        enabled = true
    }

    prepareSandbox {
        from("${project.projectDir}/src/main/resources") {
            into("${project.name}/resources")
        }
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf("default"))
    }

    runIde {
        // Increase available memory
        jvmArgs("-Xmx2g")
        autoReloadPlugins.set(true)
    }
} 