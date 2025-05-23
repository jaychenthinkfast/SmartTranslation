plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.1"
}

group = "com.github.smarttranslation"
version = "1.2.2"

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
        untilBuild.set("251.*")
        
        pluginDescription.set("""
            
            
            <h2>SmartTranslation</h2>
            <p>SmartTranslation 是一款为 JetBrains IDE 开发的智能翻译插件，提供便捷的代码注释和文档翻译功能。</p>
            
            <h3>功能特性:</h3>
            <ul>
                <li><b>高质量的 AI 翻译</b>
                    <ul>
                        <li>智谱 GLM-4 翻译（默认，免费使用，提供免密钥体验版，<a href="https://www.bigmodel.cn/invite?icode=k7Ec6USMTbEd4du4ZxULXpmwcr074zMJTpgMb8zZZvg%3D">注册获取密钥可获得稳定使用</a>）</li>
                        <li>DeepSeek AI 翻译（需要 API 密钥和充值）</li>
                        <li>支持中英文互译，准确度高</li>
                    </ul>
                </li>
                <li><b>便捷翻译操作</b>
                    <ul>
                        <li>编辑器中选择文本后使用快捷键翻译</li>
                        <li>编辑器右键菜单快速访问翻译功能</li>
                        <li>自定义快捷键支持</li>
                    </ul>
                </li>
                <li><b>个性化设置</b>
                    <ul>
                        <li>选择首选的翻译引擎</li>
                        <li>中文和英文互译</li>
                        <li>配置 API 密钥（可选，注册后可获得更稳定的服务）</li>
                        <li>可调整连接和读取超时设置</li>
                    </ul>
                </li>
            </ul>
            <hr>
            <h2>SmartTranslation</h2>
            <p>SmartTranslation is an intelligent translation plugin developed for JetBrains IDEs, providing convenient code comment and document translation functions.</p>
            
            <h3>Main Features:</h3>
            <ul>
                <li><b>High-quality AI translation</b>
                    <ul>
                        <li>Zhipu GLM-4 Translation (Default, free to use with keyless trial version, <a href="https://www.bigmodel.cn/invite?icode=k7Ec6USMTbEd4du4ZxULXpmwcr074zMJTpgMb8zZZvg%3D">register for more stable use</a>)</li>
                        <li>DeepSeek AI Translation (API key and payment required)</li>
                        <li>Support for Chinese-English translation with high accuracy</li>
                    </ul>
                </li>
                <li><b>Convenient translation operations</b>
                    <ul>
                        <li>Translate selected text in the editor using keyboard shortcuts</li>
                        <li>Quick access to translation functions from the editor's context menu</li>
                        <li>Custom keyboard shortcut support</li>
                    </ul>
                </li>
                <li><b>Personalized settings</b>
                    <ul>
                        <li>Choose preferred translation engine</li>
                        <li>Chinese and English mutual translation</li>
                        <li>Configure API keys (optional, registration provides more stable service)</li>
                        <li>Adjustable connection and read timeout settings</li>
                    </ul>
                </li>
            </ul>
            
            
        """.trimIndent())
        
        changeNotes.set("""
            <h3>1.2.2 版本更新:</h3>
            <ul>
                <li>支持最新版 GoLand IDE</li>
                <li>修复 IDE 版本兼容性问题</li>
                <li>优化插件稳定性</li>
            </ul>
            
            <h3>Version 1.2.2 Updates:</h3>
            <ul>
                <li>Support for the latest GoLand IDE version</li>
                <li>Fixed IDE version compatibility issues</li>
                <li>Improved plugin stability</li>
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