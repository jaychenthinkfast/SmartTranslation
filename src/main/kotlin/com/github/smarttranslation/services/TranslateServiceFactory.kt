package com.github.smarttranslation.services

import com.github.smarttranslation.services.impl.*
import com.github.smarttranslation.settings.AppSettingsState

/**
 * 翻译服务工厂类
 */
object TranslateServiceFactory {
    
    // 缓存各个翻译服务的实例
    private val serviceCache = mutableMapOf<String, TranslateService>()
    
    /**
     * 获取默认翻译服务
     * @return 默认翻译服务
     */
    fun getDefaultService(): TranslateService {
        val settings = AppSettingsState.getInstance()
        return getService(settings.defaultTranslateEngine)
    }
    
    /**
     * 获取指定翻译服务
     * @param engine 翻译引擎名称
     * @return 翻译服务
     */
    fun getService(engine: String): TranslateService {
        // 先从缓存中获取
        serviceCache[engine]?.let { return it }
        
        // 创建新的实例
        val service = when (engine) {
            "Google" -> GoogleTranslateService()
            "DeepSeek" -> DeepSeekTranslateService()
            else -> GoogleTranslateService() // 默认使用Google翻译
        }
        
        // 缓存并返回
        serviceCache[engine] = service
        return service
    }
    
    /**
     * 获取所有可用的翻译服务
     * @return 翻译服务映射表
     */
    fun getAllServices(): Map<String, TranslateService> {
        val engines = listOf("Google", "DeepSeek")
        
        // 确保所有服务都被创建并缓存
        engines.forEach { getService(it) }
        
        return serviceCache.toMap()
    }
    
    /**
     * 重置服务缓存
     */
    fun resetCache() {
        serviceCache.clear()
    }
} 