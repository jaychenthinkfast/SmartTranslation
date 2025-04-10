package com.github.smarttranslation.settings

import com.github.smarttranslation.services.TranslateServiceFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.*

/**
 * 应用程序设置组件UI
 */
class AppSettingsComponent {
    private val panel: JPanel
    
    // 翻译引擎设置
    private val engineComboBox = JComboBox(TranslateServiceFactory.getAvailableEngines().toTypedArray())
    
    // DeepSeek API密钥输入
    private val deepSeekApiKeyField = JBTextField()
    
    // 超时设置
    private val connectTimeoutField = JBTextField()
    private val readTimeoutField = JBTextField()
    
    /**
     * 初始化设置面板
     */
    init {
        // 创建设置界面
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("翻译引擎:"), engineComboBox, 1, false)
            .addComponent(JBLabel("DeepSeek是一个高质量的AI翻译服务，需要API密钥"), 5)
            .addSeparator(10)
            .addLabeledComponent(JBLabel("DeepSeek API密钥:"), deepSeekApiKeyField, 1, false)
            .addComponent(JBLabel("使用DeepSeek翻译服务需要配置API密钥"), 5)
            .addSeparator(10)
            .addLabeledComponent(JBLabel("连接超时(秒):"), connectTimeoutField, 1, false)
            .addComponent(JBLabel("建议值：5-15秒，网络不稳定时可适当增加"), 5)
            .addLabeledComponent(JBLabel("读取超时(秒):"), readTimeoutField, 1, false)
            .addComponent(JBLabel("建议值：10-30秒，翻译长文本时可适当增加"), 5)
            .addComponent(JBLabel("提示：增加超时时间可以减少翻译失败率，但会增加等待时间"), 5)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
    
    /**
     * 获取首选焦点组件
     */
    fun getPreferredFocusComponent(): JComponent {
        return engineComboBox
    }
    
    /**
     * 获取设置面板
     */
    fun getPanel(): JPanel {
        return panel
    }
    
    /**
     * 获取选择的翻译引擎
     */
    fun getTranslateEngine(): String {
        return engineComboBox.selectedItem as String
    }
    
    /**
     * 设置翻译引擎
     */
    fun setTranslateEngine(engine: String) {
        engineComboBox.selectedItem = engine
    }
    
    /**
     * 获取DeepSeek API密钥
     */
    fun getDeepSeekApiKey(): String {
        return deepSeekApiKeyField.text
    }
    
    /**
     * 设置DeepSeek API密钥
     */
    fun setDeepSeekApiKey(apiKey: String) {
        deepSeekApiKeyField.text = apiKey
    }
    
    /**
     * 获取连接超时时间
     */
    fun getConnectTimeout(): Int {
        val value = connectTimeoutField.text.toIntOrNull() ?: 0
        // 确保合理的范围
        return when {
            value < 1 -> 5  // 最小1秒，默认5秒
            value > 60 -> 60 // 最大60秒
            else -> value
        }
    }
    
    /**
     * 设置连接超时时间
     */
    fun setConnectTimeout(timeout: Int) {
        connectTimeoutField.text = timeout.toString()
    }
    
    /**
     * 获取读取超时时间
     */
    fun getReadTimeout(): Int {
        val value = readTimeoutField.text.toIntOrNull() ?: 0
        // 确保合理的范围
        return when {
            value < 1 -> 10  // 最小1秒，默认10秒
            value > 120 -> 120 // 最大120秒
            else -> value
        }
    }
    
    /**
     * 设置读取超时时间
     */
    fun setReadTimeout(timeout: Int) {
        readTimeoutField.text = timeout.toString()
    }
} 