package com.github.smarttranslation.ui

import com.github.smarttranslation.model.TranslateResult
import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

/**
 * 翻译结果面板
 */
class TranslateResultPanel(private val result: TranslateResult) : JPanel() {
    val preferredFocusComponent: JComponent
    
    init {
        layout = BorderLayout(10, 10)
        border = JBUI.Borders.empty(10)
        
        // 如果有错误，显示错误信息
        if (result.error != null) {
            add(createErrorPanel(result.error), BorderLayout.CENTER)
            preferredFocusComponent = this
        } else {
            // 创建原文面板
            val originalPanel = createTextPanel(
                "原文 (${result.sourceLanguage}):",
                result.originalText,
                false
            )
            
            // 创建译文面板
            val translatedPanel = createTextPanel(
                "译文 (${result.targetLanguage}):",
                result.translatedText,
                true
            )
            
            // 创建主内容面板
            val contentPanel = JPanel(BorderLayout(0, 10))
            contentPanel.add(originalPanel, BorderLayout.NORTH)
            contentPanel.add(translatedPanel, BorderLayout.CENTER)
            
            // 添加到滚动面板
            val scrollPane = JBScrollPane(contentPanel)
            scrollPane.border = BorderFactory.createEmptyBorder()
            add(scrollPane, BorderLayout.CENTER)
            
            // 创建底部工具栏
            val toolbarPanel = createToolbarPanel()
            add(toolbarPanel, BorderLayout.SOUTH)
            
            preferredFocusComponent = translatedPanel
        }
        
        // 设置首选大小
        preferredSize = Dimension(500, 300)
    }
    
    /**
     * 创建文本面板
     */
    private fun createTextPanel(title: String, content: String, isCopyable: Boolean): JPanel {
        val panel = JPanel(BorderLayout(0, 5))
        panel.border = JBUI.Borders.empty(5)
        
        // 标题标签
        val titleLabel = JBLabel(title)
        titleLabel.font = UIUtil.getLabelFont().deriveFont(UIUtil.getLabelFont().size + 1f)
        
        // 内容文本区域
        val textArea = JTextArea(content)
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.isEditable = false
        textArea.background = UIUtil.getPanelBackground()
        
        // 如果内容可复制，添加复制按钮
        if (isCopyable) {
            val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
            headerPanel.add(titleLabel)
            
            val copyButton = JButton("复制", AllIcons.Actions.Copy)
            copyButton.addActionListener {
                val selection = StringSelection(content)
                Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
                JOptionPane.showMessageDialog(
                    this,
                    "已复制到剪贴板",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
            
            val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0))
            buttonPanel.add(copyButton)
            
            val topPanel = JPanel(BorderLayout())
            topPanel.add(headerPanel, BorderLayout.WEST)
            topPanel.add(buttonPanel, BorderLayout.EAST)
            
            panel.add(topPanel, BorderLayout.NORTH)
        } else {
            panel.add(titleLabel, BorderLayout.NORTH)
        }
        
        panel.add(JBScrollPane(textArea), BorderLayout.CENTER)
        return panel
    }
    
    /**
     * 创建错误面板
     */
    private fun createErrorPanel(errorMessage: String): JPanel {
        val panel = JPanel(BorderLayout(0, 10))
        panel.border = JBUI.Borders.empty(10)
        
        val errorIcon = JBLabel(AllIcons.General.Error)
        val errorLabel = JBLabel("翻译出错", SwingConstants.CENTER)
        errorLabel.font = UIUtil.getLabelFont().deriveFont(UIUtil.getLabelFont().size + 2f)
        errorLabel.foreground = JBColor.RED
        
        val errorTextArea = JTextArea(errorMessage)
        errorTextArea.lineWrap = true
        errorTextArea.wrapStyleWord = true
        errorTextArea.isEditable = false
        errorTextArea.background = UIUtil.getPanelBackground()
        
        val topPanel = JPanel()
        topPanel.add(errorIcon)
        topPanel.add(errorLabel)
        
        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(JBScrollPane(errorTextArea), BorderLayout.CENTER)
        
        return panel
    }
    
    /**
     * 创建底部工具栏
     */
    private fun createToolbarPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.RIGHT))
        
        // 添加使用的翻译引擎标签
        val engineLabel = JBLabel("翻译引擎: ${result.engine}")
        panel.add(engineLabel)
        
        return panel
    }
} 