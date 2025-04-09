# SmartTranslation

SmartTranslation 是一款为 JetBrains IDE 开发的智能翻译插件，支持多种翻译引擎，提供便捷的代码注释和文档翻译功能。

## 功能特性

- **多种翻译引擎支持**：
  - Google 翻译（无需 API 密钥）
  - DeepSeek AI 翻译（需要 API 密钥）
  - 可扩展架构，支持未来添加更多翻译服务

- **便捷翻译操作**：
  - 编辑器中选择文本后使用快捷键翻译
  - 编辑器右键菜单快速访问翻译功能
  - 自定义快捷键支持

- **个性化设置**：
  - 自定义目标语言和源语言
  - 配置 API 密钥
  - 翻译历史记录管理

## 安装方法

### 从 JetBrains 插件市场安装

1. 在 IDE 中打开 Settings/Preferences
2. 选择 Plugins > Marketplace
3. 搜索 "SmartTranslation"
4. 点击 Install 按钮
5. 重启 IDE

### 手动安装

1. 从 [Releases](https://github.com/jaychenthinkfast/SmartTranslation/releases) 页面下载最新版本的插件 ZIP 文件
2. 在 IDE 中打开 Settings/Preferences
3. 选择 Plugins > ⚙️ > Install Plugin from Disk...
4. 选择下载的 ZIP 文件
5. 重启 IDE

## 使用方法

### 快速翻译

1. 在编辑器中选择要翻译的文本
2. 使用默认快捷键 `Ctrl+Alt+T`（Windows/Linux）或 `⌘⌥T`（macOS）
3. 或者右键点击选中文本，选择 "SmartTranslation"

### 配置插件

1. 在 IDE 中打开 Settings/Preferences
2. 导航到 Tools > Smart Translation
3. 配置以下选项：
   - 默认翻译引擎
   - API 密钥（如果使用 DeepSeek 翻译）
   - 目标语言和源语言
   - 历史记录大小
   - 自定义快捷键

## 支持的语言

插件支持多种语言，包括但不限于：
- 中文（简体和繁体）
- 英语
- 日语
- 韩语
- 法语
- 德语
- 西班牙语
- 意大利语
- 俄语
- 葡萄牙语
- 等

## 开发指南

### 环境要求

- JDK 17 或更高版本
- Gradle 7.6 或更高版本
- IntelliJ IDEA（推荐）

### 构建项目

```bash
# 克隆仓库
git clone https://github.com/jaychenthinkfast/SmartTranslation.git
cd SmartTranslation

# 构建插件
./gradlew buildPlugin
```

构建成功后，插件会位于 `build/distributions` 目录中。

### 运行和调试

```bash
# 启动 IDE 的沙箱实例来测试插件
./gradlew runIde
```

### 发布插件

```bash
# 设置发布令牌
export PUBLISH_TOKEN="your-token-here"

# 发布插件
./gradlew publishPlugin
```

## 添加新的翻译服务

要添加新的翻译服务，请遵循以下步骤：

1. 在 `services/impl` 包中创建一个新的翻译服务实现类，实现 `TranslateService` 接口
2. 在 `TranslateServiceFactory` 中注册新的翻译服务
3. 在设置组件中添加相关配置选项

## 许可证

[MIT License](LICENSE) 