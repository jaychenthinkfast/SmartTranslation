package com.github.smarttranslation.model

/**
 * 翻译结果模型类
 */
data class TranslateResult(
    val originalText: String,            // 原始文本
    val translatedText: String,          // 翻译后的文本
    val sourceLanguage: String,          // 源语言代码
    val targetLanguage: String,          // 目标语言代码
    val engine: String,                  // 翻译引擎
    val timestamp: Long = System.currentTimeMillis(),  // 翻译时间戳
    val alternatives: List<String> = emptyList(),      // 可选的翻译结果
    val error: String? = null            // 错误信息，如果有的话
) 