package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgJavaClientConfigTypeEnum;
import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgTargetRuntimeEnum;
import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
    private JPanel examplePostfixPanel = new JPanel();
    private JTextField examplePostfixField = new JTextField(10);
    private JTextField xmlPackageField = new JTextField(10);

    private JComboBox<String> mbgTargetRuntimeBox = new ComboBox<>();
    private JComboBox<String> mbgJavaClientTypeBox = new ComboBox<>();

    private JCheckBox commentBox = new JCheckBox("Comment");
    private JCheckBox overrideBox = new JCheckBox("Overwrite");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private final JCheckBox useSchemaPrefixBox = new JCheckBox("Use Schema Prefix");
    private JCheckBox annotationDAOBox = new JCheckBox("Repository Annotation");
    private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent Interface");
    private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private JCheckBox annotationBox = new JCheckBox("JPA Annotation");
    private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias");
    private JCheckBox useExampleBox = new JCheckBox("Use Example");
    private JCheckBox lombokAnnotationBox = new JCheckBox("Lombok");
    private JCheckBox lombokBuilderAnnotationBox = new JCheckBox("Lombok Builder");


    private MyBatisGeneratorConfiguration config;

    public GeneratorSettingUI() {
        setContentPane(contentPanel);
    }


    public void createUI(Project project) {
        this.project = project;
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        config = MyBatisGeneratorConfiguration.getInstance(project);

        this.initMbgTargetRuntimePanel();
        this.initPathPanel();
        this.initXmlPackagePanel();
        this.initPostfixPanel();
        this.initOptionsPanel();
        this.initClearCachePanel();
        this.reset();
    }

    private void initMbgTargetRuntimePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 2, 100, 0));

        // mbgTargetRuntime
        JPanel runtimePanel = new JPanel();
        runtimePanel.setLayout(new BoxLayout(runtimePanel, BoxLayout.X_AXIS));
        JLabel runtimeLabel = new JLabel("Target Runtime:");
        runtimePanel.add(runtimeLabel);
        for (MbgTargetRuntimeEnum value : MbgTargetRuntimeEnum.values()) {
            mbgTargetRuntimeBox.addItem(value.getName());
        }
        if (StringUtils.isNotBlank(config.getGlobalConfig().getMgbTargetRuntime())) {
            mbgTargetRuntimeBox.setSelectedItem(config.getGlobalConfig().getMgbTargetRuntime());
        }else {
            mbgTargetRuntimeBox.setSelectedIndex(0);
        }
        mbgTargetRuntimeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (Objects.equals(event.getStateChange(), ItemEvent.SELECTED)) {
                    // 根据当前选中的runtime 修改可供选择的client type
                    MbgTargetRuntimeEnum runtimeEnum = MbgTargetRuntimeEnum.getByName(
                        event.getItem().toString());
                    List<String> clientTypeList = MbgJavaClientConfigTypeEnum.getValuesByTargetRuntime(
                        runtimeEnum);
                    if (CollectionUtils.isNotEmpty(clientTypeList)) {
                        mbgJavaClientTypeBox.removeAllItems();
                        for (String type : clientTypeList) {
                            mbgJavaClientTypeBox.addItem(type);
                        }
                        mbgJavaClientTypeBox.getParent().setVisible(true);
                        mbgJavaClientTypeBox.setVisible(true);
                    }else {
                        mbgJavaClientTypeBox.getParent().setVisible(false);
                        mbgJavaClientTypeBox.setVisible(false);
                    }
                    // MyBatis3DynamicSql & MyBatis3Kotlin 模式下没有 example 和 xml
                    examplePostfixPanel.setVisible(mbgJavaClientTypeBox.isVisible());
                    useExampleBox.setVisible(mbgJavaClientTypeBox.isVisible());
                    xmlPackageField.getParent().setVisible(mbgJavaClientTypeBox.isVisible());
                }
            }
        });
        runtimePanel.add(mbgTargetRuntimeBox);
        jPanel.add(runtimePanel);

        // java client type
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
        JLabel clientTypeLabel = new JLabel("Client Type:");
        typePanel.add(clientTypeLabel);
        List<String> typeList = MbgJavaClientConfigTypeEnum.getValuesByTargetRuntime(
            MbgTargetRuntimeEnum.getByName(mbgTargetRuntimeBox.getSelectedItem().toString()));
        if (CollectionUtils.isNotEmpty(typeList)) {
            for (String type : typeList) {
                mbgJavaClientTypeBox.addItem(type);
            }
            if (StringUtils.isNotBlank(config.getGlobalConfig().getMgbJavaClientConfigType())) {
                mbgJavaClientTypeBox.setSelectedItem(config.getGlobalConfig().getMgbJavaClientConfigType());
            }else {
                mbgJavaClientTypeBox.setSelectedIndex(0);
            }
        }else {
            typePanel.setVisible(false);
            mbgJavaClientTypeBox.setVisible(false);
        }
        typePanel.add(mbgJavaClientTypeBox);
        jPanel.add(typePanel);

        TitledSeparator separator = new TitledSeparator();
        separator.setText("Runtime And Type");
        contentPanel.add(separator);
        contentPanel.add(jPanel);
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(6, 2, 10, 10));

        optionsPanel.add(commentBox);
        optionsPanel.add(overrideBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(annotationDAOBox);
        optionsPanel.add(useDAOExtendStyleBox);
        optionsPanel.add(jsr310SupportBox);
        optionsPanel.add(annotationBox);
        optionsPanel.add(useActualColumnNamesBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(useExampleBox);
        optionsPanel.add(lombokAnnotationBox);
        optionsPanel.add(lombokBuilderAnnotationBox);

        useExampleBox.setVisible(mbgJavaClientTypeBox.isVisible());

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
        xmlPackagePanel.setVisible(mbgJavaClientTypeBox.isVisible());

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
        examplePostfixPanel = buildPanel("Example Postfix:", examplePostfixField);
        examplePostfixPanel.setVisible(mbgJavaClientTypeBox.isVisible());



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
//        jbLabel.setPreferredSize(new Dimension(120, 20));
        jPanel.add(jbLabel);
        jPanel.add(jTextField);
        return jPanel;
    }


    public boolean isModified() {
        boolean modified = !Objects.equals(this.mbgTargetRuntimeBox.getSelectedItem(), config.getGlobalConfig().getMgbTargetRuntime());
        modified |= !Objects.equals(this.mbgJavaClientTypeBox.getSelectedItem(), config.getGlobalConfig().getMgbJavaClientConfigType());

        modified |= !this.sourcePathField.getText().equals(config.getGlobalConfig().getSourcePath());
        modified |= !this.resourcePathField.getText().equals(config.getGlobalConfig().getResourcePath());

        modified |= !this.xmlPackageField.getText().equals(config.getGlobalConfig().getDefaultXmlPackage());

        modified |= !this.tablePrefixField.getText().equals(config.getGlobalConfig().getTablePrefix());

        modified |= !this.domainPostfixField.getText().equals(config.getGlobalConfig().getDomainPostfix());
        modified |= !this.mapperPostfixField.getText().equals(config.getGlobalConfig().getMapperPostfix());
        modified |= !this.examplePostfixField.getText().equals(config.getGlobalConfig().getExamplePostfix());

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
        return modified;
    }

    public void apply() {
        GlobalConfig globalConfig = new GlobalConfig();

        globalConfig.setMgbTargetRuntime(mbgTargetRuntimeBox.getSelectedItem().toString());
        if (Objects.nonNull(mbgJavaClientTypeBox.getSelectedItem())) {
            globalConfig.setMgbJavaClientConfigType(mbgJavaClientTypeBox.getSelectedItem().toString());
        }

        globalConfig.setSourcePath(sourcePathField.getText());
        globalConfig.setResourcePath(resourcePathField.getText());

        globalConfig.setDefaultXmlPackage(xmlPackageField.getText());

        globalConfig.setTablePrefix(tablePrefixField.getText());

        globalConfig.setDomainPostfix(domainPostfixField.getText());
        globalConfig.setMapperPostfix(mapperPostfixField.getText());
        globalConfig.setExamplePostfix(examplePostfixField.getText());

        globalConfig.setComment(commentBox.getSelectedObjects() != null);
        globalConfig.setOverride(overrideBox.getSelectedObjects() != null);
        globalConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        globalConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        globalConfig.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
        globalConfig.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
        globalConfig.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
        globalConfig.setAnnotation(annotationBox.getSelectedObjects() != null);
        globalConfig.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
        globalConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        globalConfig.setUseExample(useExampleBox.getSelectedObjects() != null);
        globalConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
        globalConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);

        this.config.setGlobalConfig(globalConfig);
    }

    public void reset() {
        GlobalConfig globalConfig = config.getGlobalConfig();

        mbgTargetRuntimeBox.setSelectedItem(globalConfig.getMgbTargetRuntime());
        mbgJavaClientTypeBox.setSelectedItem(globalConfig.getMgbJavaClientConfigType());

        sourcePathField.setText(globalConfig.getSourcePath());
        resourcePathField.setText(globalConfig.getResourcePath());

        xmlPackageField.setText(globalConfig.getDefaultXmlPackage());

        tablePrefixField.setText(globalConfig.getTablePrefix());

        domainPostfixField.setText(globalConfig.getDomainPostfix());
        mapperPostfixField.setText(globalConfig.getMapperPostfix());
        examplePostfixField.setText(globalConfig.getExamplePostfix());

        commentBox.setSelected(globalConfig.isComment());
        overrideBox.setSelected(globalConfig.isOverride());
        needToStringHashcodeEqualsBox.setSelected(globalConfig.isNeedToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(globalConfig.isUseSchemaPrefix());
        annotationDAOBox.setSelected(globalConfig.isAnnotationDAO());
        useDAOExtendStyleBox.setSelected(globalConfig.isUseDAOExtendStyle());
        jsr310SupportBox.setSelected(globalConfig.isJsr310Support());
        annotationBox.setSelected(globalConfig.isAnnotation());
        useActualColumnNamesBox.setSelected(globalConfig.isUseActualColumnNames());
        useExampleBox.setSelected(globalConfig.isUseExample());
        lombokAnnotationBox.setSelected(globalConfig.isLombokAnnotation());
        lombokBuilderAnnotationBox.setSelected(globalConfig.isLombokBuilderAnnotation());
    }

    @Override
    public JPanel getContentPane() {
        return contentPanel;
    }


}
