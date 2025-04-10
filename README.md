# SmartTranslation

SmartTranslation 是一款为 JetBrains IDE 开发的智能翻译插件，提供便捷的代码注释和文档翻译功能。

## 功能特性

- **高质量的 AI 翻译**：
  - DeepSeek AI 翻译（需要 API 密钥）
  - 支持中英文互译，准确度高

- **便捷翻译操作**：
  - 编辑器中选择文本后使用快捷键翻译
  - 编辑器右键菜单快速访问翻译功能
  - 自定义快捷键支持

- **个性化设置**：
  - 中文和英文互译
  - 配置 API 密钥
  - 可调整连接和读取超时设置

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
   - DeepSeek API 密钥（必需）
   - 连接超时和读取超时设置
   - 自定义快捷键

## 支持的语言

目前插件支持中文和英文互译。

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

## DeepSeek API 密钥获取

要使用 SmartTranslation 插件，您需要获取 DeepSeek API 密钥：

1. 访问 [DeepSeek 官网](https://platform.deepseek.com/)
2. 注册/登录您的账户
3. 导航至 API 密钥管理页面
4. 创建新的 API 密钥
5. 将生成的密钥复制到插件设置中

## 注意事项

- 翻译长文本时可能需要较长时间，建议增加超时设置
- 确保网络连接稳定，尤其是当连接到国际API时
- API调用可能会产生费用，请参阅DeepSeek的定价政策

## 许可证

[MIT License](LICENSE) 