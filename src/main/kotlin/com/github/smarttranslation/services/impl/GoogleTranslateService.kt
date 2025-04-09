package com.github.smarttranslation.services.impl

import com.github.smarttranslation.model.TranslateResult
import com.github.smarttranslation.services.TranslateService
import com.github.smarttranslation.settings.AppSettingsState
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Google翻译服务实现
 */
class GoogleTranslateService : TranslateService {
    // OkHttp客户端
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    /**
     * 翻译文本
     */
    override fun translate(text: String, targetLanguage: String, sourceLanguage: String): TranslateResult {
        try {
            // 将Google Web翻译接口使用的语言代码转换
            val targetLang = mapLanguageCodeForWeb(targetLanguage)
            val sourceLang = mapLanguageCodeForWeb(sourceLanguage)
            
            // 构建请求URL
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sourceLang&tl=$targetLang&dt=t&q=$encodedText"
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            // 执行请求
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful) {
                // 解析响应结果
                val result = StringBuilder()
                val pattern = "\"([^\"]+)\"".toRegex()
                var matches = pattern.findAll(responseBody)
                var matchesList = matches.toList()
                
                if (matchesList.isNotEmpty()) {
                    // 解析翻译结果
                    val translatedParts = mutableListOf<String>()
                    var i = 0
                    while (i < matchesList.size) {
                        if (i + 2 < matchesList.size && matchesList[i + 2].value.contains("null")) {
                            translatedParts.add(matchesList[i].value.replace("\"", ""))
                            i += 3
                        } else {
                            i++
                        }
                    }
                    
                    result.append(translatedParts.joinToString(""))
                }
                
                val detectedSourceLang = if (sourceLanguage == "auto") {
                    // 获取检测到的语言
                    val langPattern = "\\[\\\"\\w{2}(-\\w{2})?\\\"".toRegex()
                    val langMatch = langPattern.find(responseBody)
                    if (langMatch != null) {
                        langMatch.value.replace("[\"", "").replace("\"", "")
                    } else {
                        sourceLanguage
                    }
                } else {
                    sourceLanguage
                }
                
                return TranslateResult(
                    originalText = text,
                    translatedText = result.toString(),
                    sourceLanguage = detectedSourceLang,
                    targetLanguage = targetLanguage,
                    engine = "Google"
                )
            } else {
                return TranslateResult(
                    originalText = text,
                    translatedText = "",
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    engine = "Google",
                    error = "请求失败：${response.code} ${response.message}"
                )
            }
        } catch (e: Exception) {
            return TranslateResult(
                originalText = text,
                translatedText = "",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                engine = "Google",
                error = "翻译异常：${e.message}"
            )
        }
    }
    
    /**
     * 将ISO语言代码映射为Google Web翻译接口使用的代码
     */
    private fun mapLanguageCodeForWeb(code: String): String {
        return when (code) {
            "zh-CN" -> "zh-CN"
            "zh-TW" -> "zh-TW"
            "en" -> "en"
            "ja" -> "ja"
            "ko" -> "ko"
            "fr" -> "fr"
            "de" -> "de"
            "es" -> "es"
            "it" -> "it"
            "ru" -> "ru"
            "pt" -> "pt"
            "auto" -> "auto"
            else -> code // 其他语言代码保持不变
        }
    }
    
    /**
     * 获取支持的语言列表
     */
    override fun getSupportedLanguages(): List<String> {
        return listOf(
            "zh-CN", "zh-TW", "en", "ja", "ko", "fr", "de", "es", 
            "it", "ru", "pt", "nl", "pl", "ar", "bg", "ca", "cs", 
            "da", "el", "et", "fi", "hi", "hr", "hu", "id", "is", 
            "lt", "lv", "ms", "mt", "no", "ro", "sk", "sl", "sr", 
            "sv", "th", "tr", "uk", "vi"
        )
    }
    
    /**
     * 检测文本语言
     */
    override fun detectLanguage(text: String): String {
        // 使用翻译接口的自动检测功能
        val result = translate(text, "en", "auto")
        return result.sourceLanguage
    }
} 