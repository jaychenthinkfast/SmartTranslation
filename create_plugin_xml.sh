#!/bin/bash

cat > src/main/resources/META-INF/plugin.xml << 'EOF'
<idea-plugin>
    <id>com.github.smarttranslation</id>
    <name>SmartTranslation</name>
    <vendor email="your-email@example.com">SmartTranslation</vendor>

    <depends>com.intellij.modules.platform</depends>
    <depends>org.jetbrains.plugins.go</depends>

    <extensions defaultExtensionNs="com.intellij">
        <!-- 持久化配置 -->
        <applicationService serviceImplementation="com.github.smarttranslation.settings.AppSettingsState"/>
        
        <!-- 设置页面 -->
        <applicationConfigurable
                parentId="tools"
                instance="com.github.smarttranslation.settings.AppSettingsConfigurable"
                id="com.github.smarttranslation.settings.AppSettingsConfigurable"
                displayName="SmartTranslation"/>
    </extensions>

    <actions>
        <!-- 翻译选中文本的动作 -->
        <action id="SmartTranslation.TranslateAction" 
                class="com.github.smarttranslation.actions.TranslateAction"
                text="Translate with SmartTranslation"
                description="Translate the selected text">
            <add-to-group group-id="EditorPopupMenu" anchor="first"/>
            <keyboard-shortcut keymap="$default" first-keystroke="ctrl alt T"/>
            <keyboard-shortcut keymap="Mac OS X" first-keystroke="meta alt T"/>
            <keyboard-shortcut keymap="Mac OS X 10.5+" first-keystroke="meta alt T"/>
        </action>
    </actions>
</idea-plugin>
EOF 