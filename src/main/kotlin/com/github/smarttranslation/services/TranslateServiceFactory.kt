package com.github.smarttranslation.services

import com.github.smarttranslation.services.impl.DeepSeekTranslateService
import com.github.smarttranslation.settings.AppSettingsState

/**
 * 创建翻译服务的工厂类
 */
object TranslateServiceFactory {
    /**
     * 根据设置获取适当的翻译引擎
     */
    fun getTranslateService(): TranslateService {
        val settings = AppSettingsState.getInstance()
        val engine = settings.translateEngine

        return when (engine) {
            "DeepSeek" -> DeepSeekTranslateService()
            else -> DeepSeekTranslateService() // 默认使用DeepSeek
        }
    }

    /**
     * 获取所有可用的翻译引擎
     */
    fun getAvailableEngines(): List<String> {
        return listOf("DeepSeek")
    }
} 