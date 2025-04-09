package com.github.smarttranslation.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * 应用设置状态类，用于持久化保存插件配置
 */
@State(
    name = "com.github.smarttranslation.settings.AppSettingsState",
    storages = [Storage("SmartTranslationSettings.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {

    // 默认翻译引擎
    var defaultTranslateEngine: String = "Google"
    
    // API密钥配置
    var deepSeekApiKey: String = ""
    
    // 翻译设置
    var targetLanguage: String = "zh-CN"  // 目标语言
    var sourceLanguage: String = "auto"   // 源语言，auto表示自动检测
    var maxHistorySize: Int = 50          // 历史记录最大数量
    
    // 快捷键设置
    var useCustomShortcut: Boolean = false
    var customShortcut: String = "ctrl alt T"

    companion object {
        fun getInstance(): AppSettingsState {
            return ApplicationManager.getApplication().getService(AppSettingsState::class.java)
        }
    }

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
} 