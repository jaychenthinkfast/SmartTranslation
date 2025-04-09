package com.github.smarttranslation.services

import com.github.smarttranslation.model.TranslateResult

/**
 * 翻译服务接口
 */
interface TranslateService {
    /**
     * 翻译文本
     * @param text 待翻译文本
     * @param targetLanguage 目标语言代码
     * @param sourceLanguage 源语言代码，默认为auto自动检测
     * @return 翻译结果
     */
    fun translate(text: String, targetLanguage: String, sourceLanguage: String = "auto"): TranslateResult
    
    /**
     * 获取支持的语言
     * @return 支持的语言代码列表
     */
    fun getSupportedLanguages(): List<String>
    
    /**
     * 检测文本语言
     * @param text 待检测文本
     * @return 检测到的语言代码
     */
    fun detectLanguage(text: String): String
} 