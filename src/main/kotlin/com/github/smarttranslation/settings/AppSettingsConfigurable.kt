package com.github.smarttranslation.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * 应用设置配置页
 */
class AppSettingsConfigurable : Configurable {
    private var settingsComponent: AppSettingsComponent? = null

    /**
     * 显示的配置名称
     */
    override fun getDisplayName(): String {
        return "SmartTranslation 翻译设置"
    }

    /**
     * 创建设置UI组件
     */
    override fun createComponent(): JComponent {
        settingsComponent = AppSettingsComponent()
        return settingsComponent!!.getPanel()
    }

    /**
     * 检查设置是否已修改
     */
    override fun isModified(): Boolean {
        val settings = AppSettingsState.getInstance()
        var modified = settingsComponent!!.getTranslateEngine() != settings.translateEngine
        modified = modified || settingsComponent!!.getDeepSeekApiKey() != settings.deepSeekApiKey
        modified = modified || settingsComponent!!.getConnectTimeout() != settings.connectTimeoutSeconds
        modified = modified || settingsComponent!!.getReadTimeout() != settings.readTimeoutSeconds
        return modified
    }

    /**
     * 应用设置更改
     */
    override fun apply() {
        val settings = AppSettingsState.getInstance()
        settings.translateEngine = settingsComponent!!.getTranslateEngine()
        settings.deepSeekApiKey = settingsComponent!!.getDeepSeekApiKey()
        settings.connectTimeoutSeconds = settingsComponent!!.getConnectTimeout()
        settings.readTimeoutSeconds = settingsComponent!!.getReadTimeout()
    }

    /**
     * 重置设置组件
     */
    override fun reset() {
        val settings = AppSettingsState.getInstance()
        settingsComponent!!.setTranslateEngine(settings.translateEngine)
        settingsComponent!!.setDeepSeekApiKey(settings.deepSeekApiKey)
        settingsComponent!!.setConnectTimeout(settings.connectTimeoutSeconds)
        settingsComponent!!.setReadTimeout(settings.readTimeoutSeconds)
    }

    /**
     * 销毁设置组件
     */
    override fun disposeUIResources() {
        settingsComponent = null
    }

    // 获取帮助主题
    override fun getHelpTopic(): String? {
        return "SmartTranslation Settings Help"
    }
} 