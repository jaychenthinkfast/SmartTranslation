plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.github.smarttranslation"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-dist") }
    mavenCentral()
}

// 配置IntelliJ平台插件扩展
intellij {
    version.set("2023.3")
    type.set("GO") // 设置为GoLand
    plugins.set(listOf("org.jetbrains.plugins.go"))
    downloadSources.set(false)
    updateSinceUntilBuild.set(false)
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // 网络请求库
    implementation("com.google.code.gson:gson:2.10.1") // JSON处理库
    implementation("org.json:json:20230227") // 添加JSON依赖
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks {
    // 设置Java编译器版本
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("243.*")
        pluginDescription.set("""
            SmartTranslation是一款专为GoLand IDE开发的智能翻译插件，支持多种翻译引擎，提供便捷的代码注释和文档翻译功能。
            
            主要功能:
            - 支持多种翻译引擎（Google、DeepL、百度、有道等）
            - 支持API密钥配置和管理
            - 支持快捷键翻译选中文本
            - 支持代码注释翻译
            - 支持Markdown文档翻译
            - 支持翻译历史记录
            - 支持自定义翻译规则
        """.trimIndent())
        changeNotes.set("""
            初始版本发布
            - 支持基础翻译功能
            - 支持多种翻译引擎
        """.trimIndent())
    }

    // 设置测试任务
    test {
        useJUnitPlatform()
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
} 