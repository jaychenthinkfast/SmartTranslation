package com.github.smarttranslation.settings

import com.intellij.openapi.options.Configurable
import javax.swing.*

/**
 * 设置界面配置类
 */
class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    // 创建设置组件
    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    // 判断配置是否已修改
    override fun isModified(): Boolean {
        val settings = AppSettingsState.getInstance()
        var modified = mySettingsComponent!!.defaultTranslateEngine != settings.defaultTranslateEngine
        modified = modified or (mySettingsComponent!!.deepSeekApiKey != settings.deepSeekApiKey)
        modified = modified or (mySettingsComponent!!.targetLanguage != settings.targetLanguage)
        modified = modified or (mySettingsComponent!!.sourceLanguage != settings.sourceLanguage)
        modified = modified or (mySettingsComponent!!.maxHistorySize != settings.maxHistorySize)
        modified = modified or (mySettingsComponent!!.useCustomShortcut != settings.useCustomShortcut)
        modified = modified or (mySettingsComponent!!.customShortcut != settings.customShortcut)
        return modified
    }

    // 保存设置
    override fun apply() {
        val settings = AppSettingsState.getInstance()
        settings.defaultTranslateEngine = mySettingsComponent!!.defaultTranslateEngine
        settings.deepSeekApiKey = mySettingsComponent!!.deepSeekApiKey
        settings.targetLanguage = mySettingsComponent!!.targetLanguage
        settings.sourceLanguage = mySettingsComponent!!.sourceLanguage
        settings.maxHistorySize = mySettingsComponent!!.maxHistorySize
        settings.useCustomShortcut = mySettingsComponent!!.useCustomShortcut
        settings.customShortcut = mySettingsComponent!!.customShortcut
    }

    // 重置设置
    override fun reset() {
        val settings = AppSettingsState.getInstance()
        mySettingsComponent!!.defaultTranslateEngine = settings.defaultTranslateEngine
        mySettingsComponent!!.deepSeekApiKey = settings.deepSeekApiKey
        mySettingsComponent!!.targetLanguage = settings.targetLanguage
        mySettingsComponent!!.sourceLanguage = settings.sourceLanguage
        mySettingsComponent!!.maxHistorySize = settings.maxHistorySize
        mySettingsComponent!!.useCustomShortcut = settings.useCustomShortcut
        mySettingsComponent!!.customShortcut = settings.customShortcut
    }

    // 释放资源
    override fun disposeUIResources() {
        mySettingsComponent = null
    }

    // 设置名称
    override fun getDisplayName(): String {
        return "SmartTranslation"
    }
} 