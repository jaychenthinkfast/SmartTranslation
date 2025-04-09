package com.github.smarttranslation.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UI
import javax.swing.*

/**
 * 设置UI组件类
 */
class AppSettingsComponent {
    private val engineComboBox = JComboBox(arrayOf("Google", "DeepSeek"))
    private val targetLanguageComboBox = JComboBox(arrayOf("zh-CN", "en", "ja", "ko", "fr", "ru", "de"))
    private val sourceLanguageComboBox = JComboBox(arrayOf("auto", "zh-CN", "en", "ja", "ko", "fr", "ru", "de"))
    private val historySlider = JSlider(10, 200, 50)
    
    private val deepSeekApiKeyField = JBPasswordField()
    
    private val useCustomShortcutCheckBox = JBCheckBox("使用自定义快捷键")
    private val customShortcutField = JBTextField()
    
    // 主面板
    val panel: JPanel

    init {
        // 翻译引擎面板
        val enginePanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("默认翻译引擎:"), engineComboBox, 1, false)
            .addSeparator()
            .addComponent(JBLabel("Google 翻译不需要 API 密钥"))
            .addLabeledComponent(JBLabel("DeepSeek API 密钥:"), deepSeekApiKeyField, 1, false)
            .panel
            
        // 设置面板
        val settingsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("目标语言:"), targetLanguageComboBox, 1, false)
            .addLabeledComponent(JBLabel("源语言:"), sourceLanguageComboBox, 1, false)
            .addLabeledComponent(JBLabel("历史记录最大数量:"), historySlider, 1, false)
            .addComponent(useCustomShortcutCheckBox)
            .addLabeledComponent(JBLabel("自定义快捷键:"), customShortcutField, 1, false)
            .panel
            
        // 提示面板
        val tipsPanel = FormBuilder.createFormBuilder()
            .addComponent(JBLabel("使用说明:"))
            .addComponent(JBLabel("1. 选择默认翻译引擎"))
            .addComponent(JBLabel("2. 如果使用 DeepSeek 翻译，需要配置 API 密钥"))
            .addComponent(JBLabel("3. 选择目标语言和源语言(auto表示自动检测)"))
            .addComponent(JBLabel("4. 在编辑器中选中文本，使用快捷键或右键菜单进行翻译"))
            .panel
            
        // 设置历史记录滑块显示当前值
        historySlider.paintTicks = true
        historySlider.paintLabels = true
        historySlider.majorTickSpacing = 50
        historySlider.minorTickSpacing = 10
        
        // 创建选项卡面板
        val tabbedPane = JTabbedPane()
        tabbedPane.addTab("翻译引擎", enginePanel)
        tabbedPane.addTab("常规设置", settingsPanel)
        tabbedPane.addTab("使用说明", tipsPanel)
        
        // 主面板
        panel = FormBuilder.createFormBuilder()
            .addComponent(tabbedPane)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    // 获取各种配置的getter/setter
    var defaultTranslateEngine: String
        get() = engineComboBox.selectedItem as String
        set(value) { engineComboBox.selectedItem = value }
        
    var deepSeekApiKey: String
        get() = String(deepSeekApiKeyField.password)
        set(value) { deepSeekApiKeyField.text = value }
        
    var targetLanguage: String
        get() = targetLanguageComboBox.selectedItem as String
        set(value) { targetLanguageComboBox.selectedItem = value }
        
    var sourceLanguage: String
        get() = sourceLanguageComboBox.selectedItem as String
        set(value) { sourceLanguageComboBox.selectedItem = value }
        
    var maxHistorySize: Int
        get() = historySlider.value
        set(value) { historySlider.value = value }
        
    var useCustomShortcut: Boolean
        get() = useCustomShortcutCheckBox.isSelected
        set(value) { useCustomShortcutCheckBox.isSelected = value }
        
    var customShortcut: String
        get() = customShortcutField.text
        set(value) { customShortcutField.text = value }
} 