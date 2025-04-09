package com.github.smarttranslation.actions

import com.github.smarttranslation.services.TranslateServiceFactory
import com.github.smarttranslation.settings.AppSettingsState
import com.github.smarttranslation.ui.TranslateResultPanel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.TextRange

/**
 * 翻译选中文本的动作类
 */
class TranslateAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        // 获取选中的文本
        val selectedText = getSelectedText(editor)
        if (selectedText.isNullOrBlank()) {
            return
        }
        
        // 获取设置
        val settings = AppSettingsState.getInstance()
        
        // 在后台线程中执行翻译
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "正在翻译...", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                
                // 使用默认翻译服务进行翻译
                val translateService = TranslateServiceFactory.getDefaultService()
                val result = translateService.translate(
                    selectedText,
                    settings.targetLanguage,
                    settings.sourceLanguage
                )
                
                // 在UI线程中显示结果
                ApplicationManager.getApplication().invokeLater {
                    showTranslateResult(project, editor, result)
                }
            }
        })
    }
    
    override fun update(e: AnActionEvent) {
        // 只有在编辑器中有选中文本时才启用该动作
        val editor = e.getData(CommonDataKeys.EDITOR)
        val hasSelection = editor != null && editor.selectionModel.hasSelection()
        e.presentation.isEnabled = hasSelection
    }
    
    /**
     * 获取编辑器中选中的文本
     */
    private fun getSelectedText(editor: Editor): String? {
        if (!editor.selectionModel.hasSelection()) {
            return null
        }
        
        val selectionStart = editor.selectionModel.selectionStart
        val selectionEnd = editor.selectionModel.selectionEnd
        
        return editor.document.getText(TextRange(selectionStart, selectionEnd))
    }
    
    /**
     * 显示翻译结果
     */
    private fun showTranslateResult(project: Project, editor: Editor, result: com.github.smarttranslation.model.TranslateResult) {
        val resultPanel = TranslateResultPanel(result)
        
        // 创建弹出窗口显示结果
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(resultPanel, resultPanel.preferredFocusComponent)
            .setTitle("翻译结果 (${result.engine})")
            .setMovable(true)
            .setResizable(true)
            .setMinSize(resultPanel.preferredSize)
            .setShowBorder(true)
            .createPopup()
            .showInBestPositionFor(editor)
    }
} 