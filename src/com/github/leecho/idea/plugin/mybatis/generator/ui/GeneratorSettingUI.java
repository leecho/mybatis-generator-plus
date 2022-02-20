package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 设置界面
 * Created by kangtian on 2018/8/3.
 */
public class GeneratorSettingUI extends JDialog {
    public JPanel contentPanel = new JBPanel<>();

    private Project project;

    private JTextField sourcePathField = new JTextField();
    private JTextField resourcePathField = new JTextField();

    private JTextField tablePrefixField = new JTextField(10);

    private JTextField domainPostfixField = new JTextField(10);
    private JTextField mapperPostfixField = new JTextField(10);
    private JTextField examplePostfixField = new JTextField(10);
    private JTextField xmlPackageField = new JTextField(10);

    private JCheckBox offsetLimitBox = new JCheckBox("Pageable");
    private JCheckBox commentBox = new JCheckBox("Comment");
    private JCheckBox overrideBox = new JCheckBox("Overwrite");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private JCheckBox useSchemaPrefixBox = new JCheckBox("Use Schema Prefix");
    private JCheckBox needForUpdateBox = new JCheckBox("Add ForUpdate");
    private JCheckBox annotationDAOBox = new JCheckBox("Repository Annotation");
    private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent Interface");
    private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private JCheckBox annotationBox = new JCheckBox("JPA Annotation");
    private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias");
    private JCheckBox useExampleBox = new JCheckBox("Use Example");
    private JCheckBox lombokAnnotationBox = new JCheckBox("Lombok");
    private JCheckBox lombokBuilderAnnotationBox = new JCheckBox("Lombok Builder");
    private JCheckBox swaggerAnnotationBox = new JCheckBox("Swagger Model");


    private MyBatisGeneratorConfiguration config;

    public GeneratorSettingUI() {
        setContentPane(contentPanel);
    }


    public void createUI(Project project) {
        this.project = project;
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        config = MyBatisGeneratorConfiguration.getInstance(project);

        this.initPathPanel();
        this.initXmlPackagePanel();
        this.initPostfixPanel();
        this.initOptionsPanel();
        this.initClearCachePanel();
       this.reset();
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(6, 2, 10, 10));

        optionsPanel.add(offsetLimitBox);
        optionsPanel.add(commentBox);
        optionsPanel.add(overrideBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(needForUpdateBox);
        optionsPanel.add(annotationDAOBox);
        optionsPanel.add(useDAOExtendStyleBox);
        optionsPanel.add(jsr310SupportBox);
        optionsPanel.add(annotationBox);
        optionsPanel.add(useActualColumnNamesBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(useExampleBox);
        optionsPanel.add(lombokAnnotationBox);
        optionsPanel.add(lombokBuilderAnnotationBox);
        optionsPanel.add(swaggerAnnotationBox);

        TitledSeparator separator = new TitledSeparator();
        separator.setText("Options");
        contentPanel.add(separator);
        contentPanel.add(optionsPanel);
    }

    private void initPathPanel() {

        JPanel sourcePathPanel = buildPanel("Source Path:", sourcePathField);

        JPanel resourcePathPanel = buildPanel("Resource Path:", resourcePathField);

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Path");
        pathPanel.add(sourcePathPanel);
        pathPanel.add(resourcePathPanel);
        contentPanel.add(separator);
        contentPanel.add(pathPanel);
    }

    private void initXmlPackagePanel() {

        JPanel xmlPackagePanel = buildPanel("xml package:", xmlPackageField);

        JPanel packagePanel = new JPanel();
        packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Package");
        packagePanel.add(xmlPackagePanel);
        contentPanel.add(separator);
        contentPanel.add(packagePanel);
    }

    private void initClearCachePanel() {

        JPanel clearCachePanel = new JPanel();
        clearCachePanel.setLayout(new BoxLayout(clearCachePanel, BoxLayout.X_AXIS));
        JButton clearCacheButton = new JButton("Clear Generate Setting");
        JBLabel clearCacheLabel = new JBLabel("");
        clearCachePanel.add(clearCacheButton);
        clearCachePanel.add(clearCacheLabel);

        clearCacheButton.addActionListener(e -> {
            int confirm = Messages.showOkCancelDialog(project, "Confirm clear generate setting?", "Mybatis Generator Plus", Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
            config.setTableConfigs(null);
            clearCacheLabel.setText("Clear generate setting successful!");
        });
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Others");
        contentPanel.add(separator);
        contentPanel.add(clearCachePanel);
    }


    private void initPostfixPanel() {

        JPanel tablePrefixPanel = buildPanel("Table Prefix:", tablePrefixField);

        JPanel prefixPanel = new JPanel();
        prefixPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Prefix");
        prefixPanel.add(tablePrefixPanel);
        contentPanel.add(separator);
        contentPanel.add(prefixPanel);

        JPanel domainPostfixPanel = buildPanel("Domain Postfix:", domainPostfixField);
        JPanel mapperPostfixPanel = buildPanel("Mapper Postfix:", mapperPostfixField);
        JPanel examplePostfixPanel = buildPanel("Example Postfix:", examplePostfixField);

        JPanel postfixPanel = new JPanel();
        postfixPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator2 = new TitledSeparator();
        separator2.setText("Postfix");
        postfixPanel.add(domainPostfixPanel);
        postfixPanel.add(mapperPostfixPanel);
        postfixPanel.add(examplePostfixPanel);
        contentPanel.add(separator2);
        contentPanel.add(postfixPanel);
    }

    private JPanel buildPanel(String text, JTextField jTextField) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
        JBLabel jbLabel = new JBLabel(text);
        jbLabel.setPreferredSize(new Dimension(200, 20));
        jPanel.add(jbLabel);
        jPanel.add(jTextField);
        return jPanel;
    }


    public boolean isModified() {
        boolean modified = !this.mapperPostfixField.getText().equals(config.getGlobalConfig().getMapperPostfix());
        modified |= !this.domainPostfixField.getText().equals(config.getGlobalConfig().getDomainPostfix());
        modified |= !this.examplePostfixField.getText().equals(config.getGlobalConfig().getExamplePostfix());
        modified |= !this.tablePrefixField.getText().equals(config.getGlobalConfig().getTablePrefix());
        modified |= !this.sourcePathField.getText().equals(config.getGlobalConfig().getSourcePath());
        modified |= !this.resourcePathField.getText().equals(config.getGlobalConfig().getResourcePath());
        modified |= !this.xmlPackageField.getText().equals(config.getGlobalConfig().getDefaultXmlPackage());
        modified |= (this.offsetLimitBox.getSelectedObjects() != null) == (config.getGlobalConfig().isOffsetLimit());
        modified |= (this.commentBox.getSelectedObjects() != null) == (config.getGlobalConfig().isComment());
        modified |= (this.overrideBox.getSelectedObjects() != null) == (config.getGlobalConfig().isOverride());
        modified |= (this.needToStringHashcodeEqualsBox.getSelectedObjects() != null) == (config.getGlobalConfig().isNeedToStringHashcodeEquals());
        modified |= (this.annotationDAOBox.getSelectedObjects() != null) == (config.getGlobalConfig().isAnnotationDAO());
        modified |= (this.useDAOExtendStyleBox.getSelectedObjects() != null) == (config.getGlobalConfig().isUseDAOExtendStyle());
        modified |= (this.jsr310SupportBox.getSelectedObjects() != null) == (config.getGlobalConfig().isJsr310Support());
        modified |= (this.annotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isAnnotation());
        modified |= (this.useActualColumnNamesBox.getSelectedObjects() != null) == (config.getGlobalConfig().isUseActualColumnNames());
        modified |= (this.useTableNameAliasBox.getSelectedObjects() != null) == (config.getGlobalConfig().isUseTableNameAlias());
        modified |= (this.useExampleBox.getSelectedObjects() != null) == (config.getGlobalConfig().isUseExample());
        modified |= (this.lombokAnnotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isLombokAnnotation());
        modified |= (this.lombokBuilderAnnotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isLombokBuilderAnnotation());
        modified |= (this.swaggerAnnotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isSwaggerAnnotation());
        return modified;
    }

    public void apply() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDomainPostfix(domainPostfixField.getText());
        globalConfig.setMapperPostfix(mapperPostfixField.getText());
        globalConfig.setExamplePostfix(examplePostfixField.getText());
        globalConfig.setTablePrefix(tablePrefixField.getText());
        globalConfig.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
        globalConfig.setComment(commentBox.getSelectedObjects() != null);
        globalConfig.setOverride(overrideBox.getSelectedObjects() != null);
        globalConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        globalConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        globalConfig.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
        globalConfig.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
        globalConfig.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
        globalConfig.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
        globalConfig.setAnnotation(annotationBox.getSelectedObjects() != null);
        globalConfig.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
        globalConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        globalConfig.setUseExample(useExampleBox.getSelectedObjects() != null);
        globalConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
        globalConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);
        globalConfig.setSwaggerAnnotation(swaggerAnnotationBox.getSelectedObjects() != null);

        globalConfig.setSourcePath(sourcePathField.getText());
        globalConfig.setResourcePath(resourcePathField.getText());
        globalConfig.setDefaultXmlPackage(xmlPackageField.getText());

        this.config.setGlobalConfig(globalConfig);


    }

    public void reset() {
        GlobalConfig globalConfig = config.getGlobalConfig();
        domainPostfixField.setText(globalConfig.getDomainPostfix());
        mapperPostfixField.setText(globalConfig.getMapperPostfix());
        examplePostfixField.setText(globalConfig.getExamplePostfix());
        tablePrefixField.setText(globalConfig.getTablePrefix());

        sourcePathField.setText(globalConfig.getSourcePath());
        resourcePathField.setText(globalConfig.getResourcePath());
        xmlPackageField.setText(globalConfig.getDefaultXmlPackage());

        offsetLimitBox.setSelected(globalConfig.isOffsetLimit());
        commentBox.setSelected(globalConfig.isComment());
        overrideBox.setSelected(globalConfig.isOverride());
        needToStringHashcodeEqualsBox.setSelected(globalConfig.isNeedToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(globalConfig.isUseSchemaPrefix());
        needForUpdateBox.setSelected(globalConfig.isNeedForUpdate());
        annotationDAOBox.setSelected(globalConfig.isAnnotationDAO());
        useDAOExtendStyleBox.setSelected(globalConfig.isUseDAOExtendStyle());
        jsr310SupportBox.setSelected(globalConfig.isJsr310Support());
        annotationBox.setSelected(globalConfig.isAnnotation());
        useActualColumnNamesBox.setSelected(globalConfig.isUseActualColumnNames());
        useTableNameAliasBox.setSelected(globalConfig.isUseTableNameAlias());
        useExampleBox.setSelected(globalConfig.isUseExample());
        lombokAnnotationBox.setSelected(globalConfig.isLombokAnnotation());
        lombokBuilderAnnotationBox.setSelected(globalConfig.isLombokBuilderAnnotation());
        swaggerAnnotationBox.setSelected(globalConfig.isSwaggerAnnotation());

    }

    @Override
    public JPanel getContentPane() {
        return contentPanel;
    }


}
