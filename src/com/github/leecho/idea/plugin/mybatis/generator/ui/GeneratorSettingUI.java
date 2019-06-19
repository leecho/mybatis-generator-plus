package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 设置界面
 * Created by kangtian on 2018/8/3.
 */
public class GeneratorSettingUI extends JDialog {
    public JPanel contentPanel = new JBPanel<>();

    private Project project;

    private EditorTextFieldWithBrowseButton domainPackageField;
    private EditorTextFieldWithBrowseButton mapperPackageField;
    private JTextField xmlPackageField = new JTextField();
    private EditorTextFieldWithBrowseButton examplePackageField;
    private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();

    private JTextField sourcePathField = new JTextField();
    private JTextField resourcePathField = new JTextField();

    private JTextField mapperPostfixField = new JTextField(10);
    private JTextField examplePostfixField = new JTextField(10);

    private JCheckBox offsetLimitBox = new JCheckBox("Pageable)");
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
    private JCheckBox mysql8Box = new JCheckBox("MySQL 8");
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
        this.initPostfixPanel();
        this.initPackagePanel();
        this.initOptionsPanel();
        this.initClearCachePanel();
       this.reset();
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(8, 2, 10, 10));

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
        optionsPanel.add(mysql8Box);
        optionsPanel.add(lombokAnnotationBox);
        optionsPanel.add(lombokBuilderAnnotationBox);
        optionsPanel.add(swaggerAnnotationBox);

        TitledSeparator separator = new TitledSeparator();
        separator.setText("Options");
        contentPanel.add(separator);
        contentPanel.add(optionsPanel);
    }

    private void initPathPanel() {

        JPanel sourcePathPanel = new JPanel();
        sourcePathPanel.setLayout(new BoxLayout(sourcePathPanel, BoxLayout.X_AXIS));
        JBLabel sourcePathLabel = new JBLabel("Source Path:");
        sourcePathLabel.setPreferredSize(new Dimension(200, 20));
        sourcePathPanel.add(sourcePathLabel);
        sourcePathPanel.add(sourcePathField);

        JPanel resourcePathPanel = new JPanel();
        resourcePathPanel.setLayout(new BoxLayout(resourcePathPanel, BoxLayout.X_AXIS));
        JBLabel resourcePathLabel = new JBLabel("Resource Path:");
        resourcePathLabel.setPreferredSize(new Dimension(200, 20));
        resourcePathPanel.add(resourcePathLabel);
        resourcePathPanel.add(resourcePathField);

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Path");
        pathPanel.add(sourcePathPanel);
        pathPanel.add(resourcePathPanel);
        contentPanel.add(separator);
        contentPanel.add(pathPanel);
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

        JPanel mapperPostfixPanel = new JPanel();
        mapperPostfixPanel.setLayout(new BoxLayout(mapperPostfixPanel, BoxLayout.X_AXIS));
        JBLabel mapperPostfixLabel = new JBLabel("Mapper Postfix:");
        mapperPostfixLabel.setPreferredSize(new Dimension(200, 20));
        mapperPostfixPanel.add(mapperPostfixLabel);
        mapperPostfixPanel.add(mapperPostfixField);

        JPanel examplePostfixPanel = new JPanel();
        examplePostfixPanel.setLayout(new BoxLayout(examplePostfixPanel, BoxLayout.X_AXIS));
        JBLabel examplePostfixLabel = new JBLabel("Example Postfix:");
        examplePostfixLabel.setPreferredSize(new Dimension(200, 20));
        examplePostfixPanel.add(examplePostfixLabel);
        examplePostfixPanel.add(examplePostfixField);

        JPanel postfixPanel = new JPanel();
        postfixPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Postfix");
        postfixPanel.add(mapperPostfixPanel);
        postfixPanel.add(examplePostfixPanel);
        contentPanel.add(separator);
        contentPanel.add(postfixPanel);
    }

    private void initPackagePanel() {

        GlobalConfig globalConfig = config.getGlobalConfig();

        JPanel projectRootPanel = new JPanel();
        projectRootPanel.setLayout(new BoxLayout(projectRootPanel, BoxLayout.X_AXIS));
        JBLabel projectRootLabel = new JBLabel("Module Root:");
        projectRootLabel.setPreferredSize(new Dimension(200, 20));
        moduleRootField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                moduleRootField.setText(moduleRootField.getText().replaceAll("\\\\", "/"));
            }
        });
        if (globalConfig != null && !StringUtils.isEmpty(globalConfig.getModuleRootPath())) {
            moduleRootField.setText(globalConfig.getModuleRootPath());
        } else {
            moduleRootField.setText(project.getBasePath());
        }
        projectRootPanel.add(projectRootLabel);
        projectRootPanel.add(moduleRootField);

        JPanel entityPackagePanel = new JPanel();
        entityPackagePanel.setLayout(new BoxLayout(entityPackagePanel, BoxLayout.X_AXIS));
        JBLabel entityPackageLabel = new JBLabel("Domain Package:");
        entityPackageLabel.setPreferredSize(new Dimension(200, 20));
        domainPackageField = new EditorTextFieldWithBrowseButton(project, false);
        domainPackageField.addActionListener(e -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("Select Domain Package", project);
            chooser.selectPackage(domainPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            domainPackageField.setText(packageName);
        });
        entityPackagePanel.add(entityPackageLabel);
        entityPackagePanel.add(domainPackageField);

        JPanel mapperPackagePanel = new JPanel();
        mapperPackagePanel.setLayout(new BoxLayout(mapperPackagePanel, BoxLayout.X_AXIS));
        JLabel mapperPackageLabel = new JLabel("Mapper Package:");
        mapperPackageLabel.setPreferredSize(new Dimension(200, 20));
        mapperPackageField = new EditorTextFieldWithBrowseButton(project, false);
        mapperPackageField.addActionListener(e -> {
            final PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Mapper Package", project);
            packageChooserDialog.selectPackage(mapperPackageField.getText());
            packageChooserDialog.show();
            final PsiPackage psiPackage = packageChooserDialog.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            mapperPackageField.setText(packageName);
        });
        mapperPackagePanel.add(mapperPackageLabel);
        mapperPackagePanel.add(mapperPackageField);

        JPanel examplePackagePanel = new JPanel();
        examplePackagePanel.setLayout(new BoxLayout(examplePackagePanel, BoxLayout.X_AXIS));
        JLabel examplePackageLabel = new JLabel("Example Package:");
        examplePackageLabel.setPreferredSize(new Dimension(200, 20));
        examplePackageField = new EditorTextFieldWithBrowseButton(project, false);
        examplePackageField.addActionListener(e -> {
            final PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Example Package", project);
            packageChooserDialog.selectPackage(examplePackageField.getText());
            packageChooserDialog.show();
            final PsiPackage psiPackage = packageChooserDialog.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            examplePackageField.setText(packageName);
        });
        examplePackagePanel.add(examplePackageLabel);
        examplePackagePanel.add(examplePackageField);

        JPanel xmlPackagePanel = new JPanel();
        xmlPackagePanel.setLayout(new BoxLayout(xmlPackagePanel, BoxLayout.X_AXIS));
        JLabel xmlPackageLabel = new JLabel("Xml Package:");
        xmlPackageLabel.setPreferredSize(new Dimension(200, 20));
        xmlPackageField.setText(globalConfig.getXmlPackage());
        xmlPackagePanel.add(xmlPackageLabel);
        xmlPackagePanel.add(xmlPackageField);

        JPanel packagePanel = new JPanel();
        packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        packagePanel.add(projectRootPanel);
        packagePanel.add(entityPackagePanel);
        packagePanel.add(mapperPackagePanel);
        packagePanel.add(examplePackagePanel);
        packagePanel.add(xmlPackagePanel);

        TitledSeparator separator = new TitledSeparator();
        separator.setText("Package");
        contentPanel.add(separator);
        contentPanel.add(packagePanel);
    }

    public boolean isModified() {
        boolean modified = !this.domainPackageField.getText().equals(config.getGlobalConfig().getDomainPackage());
        modified |= !this.moduleRootField.getText().equals(config.getGlobalConfig().getModuleRootPath());
        modified |= !this.mapperPackageField.getText().equals(config.getGlobalConfig().getMapperPackage());
        modified |= !this.xmlPackageField.getText().equals(config.getGlobalConfig().getXmlPackage());
        modified |= !this.examplePackageField.getText().equals(config.getGlobalConfig().getExamplePackage());
        modified |= !this.mapperPostfixField.getText().equals(config.getGlobalConfig().getMapperPostfix());
        modified |= !this.examplePostfixField.getText().equals(config.getGlobalConfig().getExamplePostfix());
        modified |= !this.sourcePathField.getText().equals(config.getGlobalConfig().getSourcePath());
        modified |= !this.resourcePathField.getText().equals(config.getGlobalConfig().getResourcePath());
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
        modified |= (this.mysql8Box.getSelectedObjects() != null) == (config.getGlobalConfig().isMysql8());
        modified |= (this.lombokAnnotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isLombokAnnotation());
        modified |= (this.lombokBuilderAnnotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isLombokBuilderAnnotation());
        modified |= (this.swaggerAnnotationBox.getSelectedObjects() != null) == (config.getGlobalConfig().isSwaggerAnnotation());
        return modified;
    }

    public void apply() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setModuleRootPath(moduleRootField.getText());
        globalConfig.setMapperPostfix(mapperPostfixField.getText());
        globalConfig.setExamplePostfix(examplePostfixField.getText());
        globalConfig.setDomainPackage(domainPackageField.getText());
        globalConfig.setMapperPackage(mapperPackageField.getText());
        globalConfig.setExamplePackage(examplePackageField.getText());
        globalConfig.setXmlPackage(xmlPackageField.getText());
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
        globalConfig.setMysql8(mysql8Box.getSelectedObjects() != null);
        globalConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
        globalConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);
        globalConfig.setSwaggerAnnotation(swaggerAnnotationBox.getSelectedObjects() != null);

        globalConfig.setSourcePath(sourcePathField.getText());
        globalConfig.setResourcePath(resourcePathField.getText());

        this.config.setGlobalConfig(globalConfig);


    }

    public void reset() {
        GlobalConfig globalConfig = config.getGlobalConfig();
        mapperPostfixField.setText(globalConfig.getMapperPostfix());
        examplePostfixField.setText(globalConfig.getExamplePostfix());
        domainPackageField.setText(globalConfig.getDomainPackage());
        mapperPackageField.setText(globalConfig.getMapperPackage());
        examplePackageField.setText(globalConfig.getExamplePackage());
        xmlPackageField.setText(globalConfig.getXmlPackage());

        sourcePathField.setText(globalConfig.getSourcePath());
        resourcePathField.setText(globalConfig.getResourcePath());

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
        mysql8Box.setSelected(globalConfig.isMysql8());
        lombokAnnotationBox.setSelected(globalConfig.isLombokAnnotation());
        lombokBuilderAnnotationBox.setSelected(globalConfig.isLombokBuilderAnnotation());
        swaggerAnnotationBox.setSelected(globalConfig.isSwaggerAnnotation());

    }

    @Override
    public JPanel getContentPane() {
        return contentPanel;
    }


}
