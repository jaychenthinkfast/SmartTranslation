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
        untilBuild.set("241.*")
        
        pluginDescription.set("""
            SmartTranslation是一款为 JetBrains IDE 开发的智能翻译插件，支持多种翻译引擎，提供便捷的代码注释和文档翻译功能。
            
            主要功能:
            - 支持多种翻译引擎
              * Google 翻译（无需 API 密钥）
              * DeepSeek AI 翻译（需要 API 密钥）
            - 便捷翻译操作
              * 编辑器中选择文本后使用快捷键翻译
              * 编辑器右键菜单快速访问翻译功能
              * 自定义快捷键支持
            - 个性化设置
              * 自定义目标语言和源语言
              * 配置 API 密钥
              * 翻译历史记录管理
        """.trimIndent())
        
        changeNotes.set("""
            <h3>1.0.0</h3>
            <ul>
                <li>初始版本发布</li>
                <li>支持 Google 翻译（无需 API 密钥）</li>
                <li>支持 DeepSeek AI 翻译</li>
                <li>支持快捷键和右键菜单翻译</li>
                <li>支持自定义语言设置</li>
                <li>支持翻译历史记录</li>
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