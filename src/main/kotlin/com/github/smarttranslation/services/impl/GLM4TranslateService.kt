package com.github.smarttranslation.services.impl

import com.github.smarttranslation.model.TranslateResult
import com.github.smarttranslation.services.TranslateService
import com.github.smarttranslation.settings.AppSettingsState
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * 智谱GLM-4翻译服务实现
 */
class GLM4TranslateService : TranslateService {
    // OkHttp客户端 - 延迟初始化，使用最新设置
    private val client: OkHttpClient by lazy {
        val settings = AppSettingsState.getInstance()
        OkHttpClient.Builder()
            .connectTimeout(settings.connectTimeoutSeconds.toLong(), TimeUnit.SECONDS)
            .readTimeout(settings.readTimeoutSeconds.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    // 智谱API端点和代理服务端点
    private val apiUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
    private val proxyUrl = "https://glmproxy.chenjie.info/v1/chat/completions"
    
    // Gson实例用于正确处理JSON
    private val gson = Gson()
    
    /**
     * 翻译文本
     */
    override fun translate(text: String, targetLanguage: String, sourceLanguage: String): TranslateResult {
        val settings = AppSettingsState.getInstance()
        val apiKey = settings.glm4ApiKey
        
        // 检查文本是否为空
        if (text.isBlank()) {
            return TranslateResult(
                originalText = text,
                translatedText = "",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                engine = "GLM-4",
                error = "待翻译文本不能为空"
            )
        }
        
        

        
        try {
            // 获取源语言和目标语言的名称
            val sourceLangName = getLanguageName(sourceLanguage)
            val targetLangName = getLanguageName(targetLanguage)
            
            // 构建提示信息 - 明确指示目标语言
            val systemPrompt = "你是一个专业的翻译引擎，请将给定的文本翻译为${targetLangName}。请只返回翻译后的文本，不要添加任何解释或额外内容。确保翻译结果是${targetLangName}。"
            val userPrompt = if (sourceLanguage == "auto") {
                "将以下文本翻译为${targetLangName}，无论原文是什么语言：\n\n$text"
            } else {
                "将以下${sourceLangName}文本翻译为${targetLangName}：\n\n$text"
            }
            
            // 构建请求内容，根据智谱GLM-4 API格式
            val requestJson = """{
                "model": "glm-4-flash",
                "messages": [
                    {"role": "system", "content": "${cleanTextForJson(systemPrompt)}"},
                    {"role": "user", "content": "${cleanTextForJson(userPrompt)}"}
                ],
                "temperature": 0.3,
                "max_tokens": ${calculateMaxTokens(text)},
                "stream": false
            }"""
            
            // 创建请求
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toRequestBody(mediaType)
            
            // 根据是否配置API密钥决定使用哪个端点
            val url = if (apiKey.isBlank()) proxyUrl else apiUrl
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            
            // 创建带有超时的客户端
            val client = OkHttpClient.Builder()
                .connectTimeout(settings.connectTimeoutSeconds.toLong(), TimeUnit.SECONDS)
                .readTimeout(settings.readTimeoutSeconds.toLong(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
            
            val call = client.newCall(request)
            
            try {
                // 同步执行请求
                val response = call.execute()
                val responseBody = response.body?.string() ?: ""
                
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
                            engine = "GLM-4"
                        )
                    } catch (e: Exception) {
                        // JSON解析错误
                        return TranslateResult(
                            originalText = text,
                            translatedText = "",
                            sourceLanguage = sourceLanguage,
                            targetLanguage = targetLanguage,
                            engine = "GLM-4",
                            error = "解析响应失败：${e.message}"
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
                        errorDetails += "\n无法解析错误详情"
                    }
                    
                    return TranslateResult(
                        originalText = text,
                        translatedText = "",
                        sourceLanguage = sourceLanguage,
                        targetLanguage = targetLanguage,
                        engine = "GLM-4",
                        error = errorDetails
                    )
                }
            } catch (e: java.net.SocketTimeoutException) {
                // 处理超时异常
                return TranslateResult(
                    originalText = text,
                    translatedText = "",
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    engine = "GLM-4",
                    error = "请求超时，请在设置中增加超时时间或检查网络连接"
                )
            } catch (e: java.net.ConnectException) {
                // 处理连接异常
                return TranslateResult(
                    originalText = text,
                    translatedText = "",
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    engine = "GLM-4",
                    error = "连接失败，请检查网络连接和API地址"
                )
            } catch (e: java.io.IOException) {
                // 处理IO异常
                return TranslateResult(
                    originalText = text,
                    translatedText = "",
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    engine = "GLM-4",
                    error = "网络IO错误：${e.message}"
                )
            }
        } catch (e: Exception) {
            return TranslateResult(
                originalText = text,
                translatedText = "",
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                engine = "GLM-4",
                error = "翻译异常：${e.message}"
            )
        }
    }
    
    /**
     * 根据输入文本长度计算合适的最大令牌数
     * 这是为了避免超时，文本越长，需要的处理时间越长
     */
    private fun calculateMaxTokens(text: String): Int {
        val baseTokens = 1000
        // 大致估算：每个字符约占0.5-1个token，我们按保守的1:1计算
        val estimatedInputTokens = text.length
        // 输出一般是输入的2倍以内
        return minOf(baseTokens, estimatedInputTokens * 2) + 100 // 加上100作为缓冲
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
        // 检查文本是否为空
        if (text.isBlank()) {
            return "en" // 默认返回英语
        }
        
        // 简易语言检测逻辑
        // 如果文本包含较多的中文字符，判定为中文
        val chineseCharCount = text.count { it.code in 0x4E00..0x9FFF }
        val chineseRatio = chineseCharCount.toFloat() / text.length
        
        return if (chineseRatio > 0.1) {
            // 如果包含10%以上的汉字，判定为中文
            "zh-CN"
        } else {
            // 否则默认为英文
            "en"
        }
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