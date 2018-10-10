package com.jd.idea.plugin.mybatis.generator.setting;

import com.jd.idea.plugin.mybatis.generator.ui.GeneratorSettingUI;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.sun.istack.internal.NotNull;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *配置设置界面
 * Created by kangtian on 2018/7/18.
 */
public class SettingConfigurable implements SearchableConfigurable {
    private GeneratorSettingUI mainPanel;

    @SuppressWarnings("FieldCanBeLocal")
    private final Project project;


    public SettingConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Mybatis Generator";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "gene.helpTopic";
    }

    @NotNull
    @Override
    public String getId() {
        return "Mybatis.Generator.Plugin";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainPanel = new GeneratorSettingUI();
        mainPanel.createUI(project);
        return mainPanel.getContentPane();
    }

    @Override
    public boolean isModified() {
        return mainPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        mainPanel.apply();
    }

    @Override
    public void reset() {
        mainPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }
}
