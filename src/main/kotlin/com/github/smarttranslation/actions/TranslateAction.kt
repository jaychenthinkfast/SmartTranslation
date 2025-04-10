package com.github.smarttranslation.actions

import com.github.smarttranslation.services.TranslateServiceFactory
import com.github.smarttranslation.ui.TranslateResultPanel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.TextRange

/**
 * 翻译选中文本的动作
 */
class TranslateAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val selectedText = getSelectedText(editor)
        
        if (selectedText.isBlank()) {
            return
        }
        
        // 获取翻译服务
        val translateService = TranslateServiceFactory.getTranslateService()
        
        // 创建翻译结果面板
        val resultPanel = TranslateResultPanel(selectedText, translateService)
        
        // 显示弹出窗口
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(resultPanel, null)
            .setTitle("智能翻译")
            .setMovable(true)
            .setResizable(true)
            .setRequestFocus(true)
            .createPopup()
            .showInBestPositionFor(editor)
    }

    override fun update(e: AnActionEvent) {
        // 只有当编辑器中有选中文本时才启用该操作
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null && editor.selectionModel.hasSelection()
    }

    /**
     * 获取编辑器中选中的文本
     */
    private fun getSelectedText(editor: Editor): String {
        val selectionModel = editor.selectionModel
        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd
        return editor.document.getText(TextRange(startOffset, endOffset))
    }
} 