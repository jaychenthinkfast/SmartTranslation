package com.github.smarttranslation.ui

import com.github.smarttranslation.model.TranslateResult
import com.github.smarttranslation.services.TranslateService
import com.github.smarttranslation.settings.AppSettingsState
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.swing.*

/**
 * 翻译结果显示面板
 */
class TranslateResultPanel(
    private val originalText: String,
    private val translateService: TranslateService
) : JBPanel<TranslateResultPanel>() {

    // 语言代码到显示名称的映射
    private val languageDisplayNames = mapOf(
        "zh-CN" to "中文",
        "en" to "英文"
    )
    
    // 语言显示名称到代码的映射
    private val displayNameToCode = mapOf(
        "中文" to "zh-CN",
        "英文" to "en"
    )

    private val targetLanguageComboBox = ComboBox<String>()
    private val originalTextArea = JTextArea()
    private val translatedTextArea = JTextArea()
    private val statusLabel = JBLabel()
    private val copyButton = JButton("复制")
    private val retryButton = JButton("重试")
    private val progressBar = JProgressBar().apply {
        isIndeterminate = true
        isVisible = false
    }
    
    // 当前翻译任务
    private var currentTranslationTask: CompletableFuture<*>? = null
    
    init {
        layout = BorderLayout(10, 10)
        border = JBUI.Borders.empty(10)
        preferredSize = Dimension(600, 400)
        
        setupComponents()
        initializeLanguageOptions()
        performTranslation()
    }
    
    /**
     * 设置UI组件
     */
    private fun setupComponents() {
        // 顶部操作面板
        val topPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        topPanel.add(JLabel("目标语言:"))
        topPanel.add(targetLanguageComboBox)
        add(topPanel, BorderLayout.NORTH)
        
        // 原文与译文文本区域
        val textPanel = JPanel(BorderLayout(10, 10))
        
        // 原文面板
        val originalPanel = JPanel(BorderLayout())
        originalPanel.border = BorderFactory.createTitledBorder("原文")
        originalTextArea.lineWrap = true
        originalTextArea.wrapStyleWord = true
        originalTextArea.isEditable = false
        originalTextArea.text = originalText
        originalPanel.add(JBScrollPane(originalTextArea), BorderLayout.CENTER)
        
        // 译文面板
        val translatedPanel = JPanel(BorderLayout())
        translatedPanel.border = BorderFactory.createTitledBorder("译文")
        translatedTextArea.lineWrap = true
        translatedTextArea.wrapStyleWord = true
        translatedTextArea.isEditable = false
        translatedPanel.add(JBScrollPane(translatedTextArea), BorderLayout.CENTER)
        
        // 添加到文本面板
        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, originalPanel, translatedPanel)
        splitPane.resizeWeight = 0.5
        textPanel.add(splitPane, BorderLayout.CENTER)
        
        add(textPanel, BorderLayout.CENTER)
        
        // 底部状态面板
        val bottomPanel = JPanel(BorderLayout())
        bottomPanel.border = JBUI.Borders.empty(5)
        
        // 左侧状态区域
        val leftPanel = JPanel(BorderLayout(5, 0))
        progressBar.preferredSize = Dimension(100, 15)
        leftPanel.add(progressBar, BorderLayout.WEST)
        leftPanel.add(statusLabel, BorderLayout.CENTER)
        statusLabel.text = "准备翻译..."
        bottomPanel.add(leftPanel, BorderLayout.WEST)
        
        // 右侧按钮区域
        val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0))
        retryButton.addActionListener { performTranslation() }
        retryButton.isEnabled = false
        rightPanel.add(retryButton)
        
        copyButton.addActionListener { copyTranslatedText() }
        copyButton.isEnabled = false
        rightPanel.add(copyButton)
        
        bottomPanel.add(rightPanel, BorderLayout.EAST)
        
        add(bottomPanel, BorderLayout.SOUTH)
    }
    
    /**
     * 初始化语言选项
     */
    private fun initializeLanguageOptions() {
        val settings = AppSettingsState.getInstance()
        
        // 添加目标语言选项（使用显示名称）
        targetLanguageComboBox.addItem("中文")
        targetLanguageComboBox.addItem("英文")
        
        // 根据原文语言猜测目标语言
        val detectedLang = translateService.detectLanguage(originalText)
        val targetLang = if (detectedLang == "zh-CN" || detectedLang == "zh") "英文" else "中文"
        targetLanguageComboBox.selectedItem = targetLang
        
        // 添加语言切换监听器
        targetLanguageComboBox.addActionListener { performTranslation() }
    }
    
    /**
     * 执行翻译操作
     */
    private fun performTranslation() {
        // 取消正在进行的翻译
        currentTranslationTask?.cancel(true)
        
        // 更新UI状态
        statusLabel.text = "翻译中..."
        translatedTextArea.text = "正在翻译..."
        copyButton.isEnabled = false
        retryButton.isEnabled = false
        progressBar.isVisible = true
        
        // 获取超时设置
        val settings = AppSettingsState.getInstance()
        val totalTimeout = (settings.connectTimeoutSeconds + settings.readTimeoutSeconds + 2).toLong()
        
        // 获取目标语言（显示名称转换为代码）
        val targetLangName = targetLanguageComboBox.selectedItem as String
        val targetLang = displayNameToCode[targetLangName] ?: "zh-CN"
        
        // 在后台线程中执行翻译
        currentTranslationTask = CompletableFuture.supplyAsync {
            translateService.translate(originalText, targetLang, "auto")
        }.orTimeout(totalTimeout, TimeUnit.SECONDS) // 添加总体超时控制
        .thenAccept { result ->
            // 在UI线程中更新结果
            ApplicationManager.getApplication().invokeLater {
                updateTranslationResult(result)
                progressBar.isVisible = false
                retryButton.isEnabled = true
            }
        }.exceptionally { e ->
            // 处理异常
            ApplicationManager.getApplication().invokeLater {
                progressBar.isVisible = false
                
                val errorMessage = when (e) {
                    is TimeoutException -> "翻译请求超时，请在设置中增加超时时间"
                    else -> e.cause?.message ?: e.message ?: "未知错误"
                }
                
                translatedTextArea.text = ""
                statusLabel.text = "翻译失败: $errorMessage"
                copyButton.isEnabled = false
                retryButton.isEnabled = true
                
                // 如果是超时错误，提示用户调整超时设置
                if (e is TimeoutException) {
                    Messages.showWarningDialog(
                        "翻译请求超时，可能是网络问题或文本太长。\n\n" +
                        "建议：\n" +
                        "1. 在设置中增加连接和读取超时时间\n" +
                        "2. 检查网络连接\n" +
                        "3. 尝试翻译较短的文本片段",
                        "翻译超时"
                    )
                }
            }
            null
        }
    }
    
    /**
     * 更新翻译结果
     */
    private fun updateTranslationResult(result: TranslateResult) {
        if (result.error != null) {
            translatedTextArea.text = ""
            statusLabel.text = "翻译失败: ${result.error}"
            copyButton.isEnabled = false
            
            // 如果错误包含超时，提示用户调整设置
            if (result.error.contains("超时")) {
                Messages.showWarningDialog(
                    "翻译请求超时，可能是网络问题或文本太长。\n\n" +
                    "建议：\n" +
                    "1. 在设置中增加连接和读取超时时间\n" +
                    "2. 检查网络连接\n" +
                    "3. 尝试翻译较短的文本片段",
                    "翻译超时"
                )
            }
            
            return
        }
        
        translatedTextArea.text = result.translatedText
        statusLabel.text = "翻译成功 (引擎: ${result.engine})"
        copyButton.isEnabled = true
    }
    
    /**
     * 复制翻译后的文本
     */
    private fun copyTranslatedText() {
        val text = translatedTextArea.text
        if (text.isNotBlank()) {
            CopyPasteManager.getInstance().setContents(StringSelection(text))
            statusLabel.text = "已复制到剪贴板"
        }
    }
} 