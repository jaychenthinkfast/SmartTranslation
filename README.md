# SmartTranslation

SmartTranslation 是一款适用于 JetBrains IDE 的智能翻译插件，支持通过多种翻译服务进行文本翻译。

## 功能特点

- 支持 Google 翻译（无需API密钥）和 DeepSeek AI 翻译引擎
- 智能文本选择翻译
- 自定义快捷键支持
- 翻译历史记录
- 右键菜单顶部快速访问

## 安装方法

1. 下载插件 ZIP 文件 (`SmartTranslation-1.0-SNAPSHOT.zip`)
2. 打开 JetBrains IDE（如 IntelliJ IDEA、GoLand 等）
3. 进入 `Settings/Preferences` → `Plugins`
4. 点击齿轮图标，选择 `Install Plugin from Disk...`
5. 选择下载的 ZIP 文件
6. 重启 IDE

## 配置说明

### 配置设置

1. 进入 `Settings/Preferences` → `Tools` → `Smart Translation`
2. 选择默认翻译引擎：
   - Google 翻译: 无需 API 密钥
   - DeepSeek AI: 需要 API 密钥（可从 https://platform.deepseek.com/api_keys 获取）

### 翻译设置

- **目标语言**：设置翻译结果的语言
- **源语言**：设置原文语言（auto 表示自动检测）
- **历史记录最大数量**：设置保存的翻译记录数量

## 使用方法

1. 在编辑器中选中需要翻译的文本
2. 使用以下任一方式翻译：
   - 右键点击选中文本，在菜单顶部选择 `SmartTranslation`
   - 使用快捷键 `Ctrl+Alt+T`（或自定义快捷键）

## 特性改进

最新版本的改进：
- 将翻译选项移动到右键菜单顶部，方便快速访问
- 更新菜单项名称为"SmartTranslation"，更符合插件品牌
- 移除对Google翻译API密钥的依赖，使用免费翻译服务
- 优化DeepSeek翻译服务，提高稳定性

## 开发信息

- 语言：Kotlin
- 框架：JetBrains Platform SDK
- 第三方库：
  - OkHttp：用于 HTTP 请求
  - Gson：用于 JSON 处理

## 许可证

[MIT License](LICENSE)


