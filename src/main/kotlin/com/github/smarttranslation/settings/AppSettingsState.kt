package com.github.smarttranslation.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * 应用程序设置状态
 */
@State(
    name = "com.github.smarttranslation.settings.AppSettingsState",
    storages = [Storage("SmartTranslationSettings.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    // 深度搜索API密钥
    var deepSeekApiKey: String = ""

    // 翻译引擎
    var translateEngine: String = "DeepSeek"

    // 连接超时时间（秒）
    var connectTimeoutSeconds: Int = 10

    // 读取超时时间（秒）
    var readTimeoutSeconds: Int = 10

    /**
     * 获取持久化状态
     */
    override fun getState(): AppSettingsState {
        return this
    }

    /**
     * 加载持久化状态
     */
    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        /**
         * 获取实例
         */
        fun getInstance(): AppSettingsState {
            return ApplicationManager.getApplication().getService(AppSettingsState::class.java)
        }
    }
} 