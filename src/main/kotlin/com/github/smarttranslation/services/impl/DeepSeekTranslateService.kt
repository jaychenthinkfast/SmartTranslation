package com.github.smarttranslation.services.impl

import com.github.smarttranslation.model.TranslateResult
import com.github.smarttranslation.services.TranslateService
import com.github.smarttranslation.settings.AppSettingsState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * DeepSeek翻译服务实现
 */
class DeepSeekTranslateService : TranslateService {
    // OkHttp客户端
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // DeepSeek API正确的端点
    private val apiUrl = "https://api.deepseek.com/v1/chat/completions"
    
    // Gson实例用于正确处理JSON
    private val gson = Gson()
    
    /**
     * 翻译文本
     */
    override fun translate(text: String, targetLanguage: String, sourceLanguage: String): TranslateResult {
        val settings = AppSettingsState.getInstance()
        val apiKey = settings.deepSeekApiKey
        
        if (apiKey.isBlank()) {
            return TranslateResult(
                originalText = text,
                translatedText = "",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                engine = "DeepSeek",
                error = "请配置API密钥"
            )
        }
        
        try {
            // 构建提示词
            val sourceLang = if (sourceLanguage == "auto") "自动检测" else getLanguageName(sourceLanguage)
            val targetLang = getLanguageName(targetLanguage)
            
            // 清理待翻译文本，移除可能导致JSON解析错误的控制字符
            val cleanedText = cleanTextForJson(text)
            
            val prompt = if (sourceLanguage == "auto") {
                "请翻译成${targetLang}：\n\n$cleanedText"
            } else {
                "请将${sourceLang}翻译成${targetLang}：\n\n$cleanedText"
            }
            
            // 使用Gson构建JSON，避免格式问题
            val requestMap = mapOf(
                "model" to "deepseek-chat",
                "messages" to listOf(
                    mapOf("role" to "system", "content" to "你是专业翻译，直接返回翻译结果，不要解释。"),
                    mapOf("role" to "user", "content" to prompt)
                ),
                "temperature" to 0.1,
                "max_tokens" to 1000
            )
            
            val jsonBody = gson.toJson(requestMap)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toRequestBody(mediaType)
            
            // 构建请求，确保使用正确的Authorization格式
            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            
            // 同步执行请求
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            // 记录请求和响应信息，便于调试
            println("DeepSeek请求URL: $apiUrl")
            println("DeepSeek请求体: $jsonBody")
            println("DeepSeek响应状态: ${response.code}")
            println("DeepSeek响应体: $responseBody")
            
            if (response.isSuccessful) {
                try {
                    // 解析响应
                    val jsonElement = JsonParser.parseString(responseBody)
                    val choices = jsonElement.asJsonObject.getAsJsonArray("choices")
                    val choice = choices[0].asJsonObject
                    val message = choice.getAsJsonObject("message")
                    val content = message.get("content").asString
                    
                    // 处理翻译结果，提取纯净的翻译文本
                    val cleanedTranslation = content.trim()
                    
                    return TranslateResult(
                        originalText = text,
                        translatedText = cleanedTranslation,
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        engine = "DeepSeek"
                    )
                } catch (e: Exception) {
                    // JSON解析错误
                    return TranslateResult(
                        originalText = text,
                        translatedText = "",
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        engine = "DeepSeek",
                        error = "解析响应失败：${e.message}\n响应内容：$responseBody"
                    )
                }
            } else {
                // 添加更详细的错误信息
                var errorDetails = "请求失败：${response.code} ${response.message}"
                try {
                    // 尝试从响应体中解析更详细的错误信息
                    val errorJson = JsonParser.parseString(responseBody).asJsonObject
                    if (errorJson.has("error")) {
                        val error = errorJson.getAsJsonObject("error")
                        val message = if (error.has("message")) error.get("message").asString else "未知错误"
                        val type = if (error.has("type")) error.get("type").asString else "未知类型"
                        errorDetails += "\n错误类型：$type\n错误消息：$message"
                    }
                } catch (e: Exception) {
                    errorDetails += "\n无法解析错误详情：$responseBody"
                }
                
                return TranslateResult(
                    originalText = text,
                    translatedText = "",
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    engine = "DeepSeek",
                    error = errorDetails
                )
            }
        } catch (e: Exception) {
            return TranslateResult(
                originalText = text,
                translatedText = "",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                engine = "DeepSeek",
                error = "翻译异常：${e.message}"
            )
        }
    }
    
    /**
     * 清理文本，移除可能导致JSON解析错误的控制字符
     */
    private fun cleanTextForJson(text: String): String {
        return text.replace(Regex("[\\u0000-\\u001F]"), "") // 移除所有控制字符
                .replace("\\", "\\\\") // 转义反斜线
                .replace("\"", "\\\"") // 转义双引号
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
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
        // DeepSeek模型不提供单独的语言检测API，我们通过翻译时候返回源语言信息
        // 这里的实现很简单，就是将源语言设为auto，让模型自己判断
        val result = translate(text, "en", "auto")
        return result.sourceLanguage
    }
    
    /**
     * 根据语言代码获取语言名称
     */
    private fun getLanguageName(langCode: String): String {
        return when (langCode) {
            "zh-CN" -> "中文"
            "zh-TW" -> "繁体中文"
            "en" -> "英文"
            "ja" -> "日文"
            "ko" -> "韩文"
            "fr" -> "法文"
            "de" -> "德文"
            "es" -> "西班牙文"
            "it" -> "意大利文"
            "ru" -> "俄文"
            "pt" -> "葡萄牙文"
            "auto" -> "自动检测"
            else -> langCode
        }
    }
} 