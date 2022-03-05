package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgJavaClientConfigTypeEnum;
import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgTargetRuntimeEnum;
import com.github.leecho.idea.plugin.mybatis.generator.enums.PackageTypeEnum;
import com.github.leecho.idea.plugin.mybatis.generator.generate.MyBatisGenerateCommand;
import com.github.leecho.idea.plugin.mybatis.generator.model.ConnectionConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableInfo;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.github.leecho.idea.plugin.mybatis.generator.util.DatabaseUtils;
import com.github.leecho.idea.plugin.mybatis.generator.util.JTextFieldHintListener;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.database.model.NameVersion;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbNamespace;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 插件主界面
 * Created by kangtian on 2018/8/1.
 */
public class GenerateSettingUI extends DialogWrapper {

    private AnActionEvent anActionEvent;
    private Project project;
    private MyBatisGeneratorConfiguration myBatisGeneratorConfiguration;
    private PsiElement[] psiElements;
    private TableConfig tableConfig;

    private JPanel contentPane = new JBPanel<>();

    private JTextField tableNameField = new JBTextField(20);

    private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();
    private TextFieldWithBrowseButton basePackageField = new TextFieldWithBrowseButton();
    private TextFieldWithBrowseButton domainPackageField = new TextFieldWithBrowseButton();
    private TextFieldWithBrowseButton mapperPackageField = new TextFieldWithBrowseButton();
    private TextFieldWithBrowseButton examplePackageField = new TextFieldWithBrowseButton();
    private TextFieldWithBrowseButton xmlPackageField = new TextFieldWithBrowseButton();
    private JTextField mapperNameField = new JBTextField(20);
    private JTextField domainNameField = new JBTextField(20);
    private JTextField exampleNameField = new JBTextField(20);
    private JTextField primaryKeyField = new JBTextField(20);

    private JPanel examplePackagePanel = new JPanel();
    private JPanel exampleNamePanel = new JPanel();

    private JComboBox<String> mbgTargetRuntimeBox = new ComboBox<>();
    private JComboBox<String> mbgJavaClientTypeBox = new ComboBox<>();

    private JCheckBox commentBox = new JCheckBox("Comment");
    private JCheckBox overrideBox = new JCheckBox("Overwrite");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private JCheckBox useSchemaPrefixBox = new JCheckBox("Use Schema Prefix");
    private JCheckBox annotationDAOBox = new JCheckBox("Repository Annotation");
    private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent Interface");
    private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private JCheckBox annotationBox = new JCheckBox("JPA Annotation");
    private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias");
    private JCheckBox useExampleBox = new JCheckBox("Use Example");
    private JCheckBox lombokAnnotationBox = new JCheckBox("Lombok");
    private JCheckBox lombokBuilderAnnotationBox = new JCheckBox("Lombok Builder");
    private JBTabbedPane tabpanel = new JBTabbedPane();
    private String basePackageInitialPath;
    private String domainPackageInitialPath;
    private String mapperPackageInitialPath;
    private String examplePackageInitialPath;
    private String xmlPackageInitialPath;

    public GenerateSettingUI(AnActionEvent anActionEvent) {
        super(anActionEvent.getData(PlatformDataKeys.PROJECT));
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.anActionEvent = anActionEvent;
        this.project = project;
        this.myBatisGeneratorConfiguration = MyBatisGeneratorConfiguration.getInstance(project);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        GlobalConfig globalConfig = myBatisGeneratorConfiguration.getGlobalConfig();
        Map<String, TableConfig> historyConfigList = myBatisGeneratorConfiguration.getTableConfigs();

        setTitle("MyBatis Generator Plus");
        //设置大小
        pack();
        setModal(true);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String tableName = tableInfo.getTableName();
        String realTableName;
        if (globalConfig.getTablePrefix() != null && tableName.startsWith(globalConfig.getTablePrefix())) {
            realTableName = tableName.substring(globalConfig.getTablePrefix().length());
        } else {
            realTableName = tableName;
        }
        String entityName = StringUtils.dbStringToCamelStyle(realTableName);
        String primaryKey = "";
        if (tableInfo.getPrimaryKeys().size() > 0) {
            primaryKey = tableInfo.getPrimaryKeys().get(0);
        }

        initTableConfig(globalConfig, historyConfigList, tableName, primaryKey);
        VerticalFlowLayout layoutManager = new VerticalFlowLayout(VerticalFlowLayout.TOP);
        layoutManager.setHgap(0);
        layoutManager.setVgap(0);
        contentPane.setLayout(layoutManager);
        this.initHeader(tableName, primaryKey);
        this.initGeneralPanel(entityName);
        this.initOptionsPanel();
//        tabpanel.add(new ColumnTablePanel(tableConfig, tableInfo));
        contentPane.add(tabpanel);
        tabpanel.setUI(new GenerateSettingTabUI());
        contentPane.setBorder(JBUI.Borders.empty());
        this.init();
    }

    /**
     * 初始化 表相关配置
     * @param globalConfig
     * @param historyConfigList
     * @param tableName
     * @param primaryKey
     */
    private void initTableConfig(GlobalConfig globalConfig, Map<String, TableConfig> historyConfigList,
        String tableName, String primaryKey) {
        //单表时，优先使用已经存在的配置
        if (historyConfigList != null) {
            tableConfig = historyConfigList.get(tableName);
        }
        if (tableConfig == null) {
            //初始化配置
            tableConfig = new TableConfig();
            tableConfig.setSourcePath(globalConfig.getSourcePath());
            tableConfig.setResourcePath(globalConfig.getResourcePath());
            tableConfig.setXmlPackage(globalConfig.getDefaultXmlPackage());
            tableConfig.setDomainPostfix(globalConfig.getDomainPostfix());
            tableConfig.setMapperPostfix(globalConfig.getMapperPostfix());
            tableConfig.setExamplePostfix(globalConfig.getExamplePostfix());
            //默认采用 MyBatis3DynamicSql 运行时
            tableConfig.setMgbTargetRuntime(MbgTargetRuntimeEnum.MY_BATIS3_DYNAMIC_SQL.name());

            tableConfig.setComment(globalConfig.isComment());
            tableConfig.setOverride(globalConfig.isOverride());
            tableConfig.setNeedToStringHashcodeEquals(globalConfig.isNeedToStringHashcodeEquals());
            tableConfig.setUseSchemaPrefix(globalConfig.isUseSchemaPrefix());
            tableConfig.setAnnotationDAO(globalConfig.isAnnotationDAO());
            tableConfig.setUseDAOExtendStyle(globalConfig.isUseDAOExtendStyle());
            tableConfig.setJsr310Support(globalConfig.isJsr310Support());
            tableConfig.setAnnotation(globalConfig.isAnnotation());
            tableConfig.setUseActualColumnNames(globalConfig.isUseActualColumnNames());
            tableConfig.setUseExample(globalConfig.isUseExample());
            tableConfig.setLombokAnnotation(globalConfig.isLombokAnnotation());
            tableConfig.setLombokBuilderAnnotation(globalConfig.isLombokBuilderAnnotation());
            tableConfig.setPrimaryKey(primaryKey);
        }
    }

    @NotNull
    @Override
    protected DialogStyle getStyle() {
        return DialogStyle.COMPACT;
    }

    private List<String> validateSetting() {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isEmpty(moduleRootField.getText())) {
            errors.add("Module root must not be null");
        }

        if (StringUtils.isEmpty(domainNameField.getText())) {
            errors.add("Domain name must not be null");
        }

        if (StringUtils.isEmpty(mapperNameField.getText())) {
            errors.add("Mapper name must not be null");
        }

        if (StringUtils.isEmpty(domainPackageField.getText())) {
            errors.add("Domain package must not be null");
        }

        if (StringUtils.isEmpty(mapperPackageField.getText())) {
            errors.add("Mapper package must not be null");
        }

        if (StringUtils.isEmpty(xmlPackageField.getText())) {
            errors.add("Mapper xml package must not be null");
        }

        if (useExampleBox.getSelectedObjects() != null) {
            if (StringUtils.isEmpty(exampleNameField.getText())) {
                errors.add("Example name must not be null");
            }
            if (StringUtils.isEmpty(examplePackageField.getText())) {
                errors.add("Example package must not be null");
            }
        }
        return errors;
    }

    @Override
    protected void doOKAction() {

        List<String> errors = this.validateSetting();
        if (!errors.isEmpty()) {
            Messages.showMessageDialog("Invalid setting: \n" + String.join("\n", errors), "Mybatis Generator Plus", Messages.getWarningIcon());
            return;
        }

        // todo get database username password from RawConnectionConfig
        ConnectionConfig connectionConfig = getRawConnectionConfig();
        if (connectionConfig == null) {
            return;
        }
        Map<String, Credential> credentials = myBatisGeneratorConfiguration.getCredentials();
        if (MapUtils.isEmpty(credentials)) {
            credentials = new HashMap<>();
        }
        Credential credential;
        if (!credentials.containsKey(connectionConfig.getUrl())) {
            credential = new Credential(connectionConfig.getUrl());
            credentials.put(connectionConfig.getUrl(), credential);
        } else {
            credential = credentials.get(connectionConfig.getUrl());
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(credential.getUsername())
            || org.apache.commons.lang3.StringUtils.isBlank(credential.getPwd())) {
            getDatabaseCredential(credential);
            myBatisGeneratorConfiguration.setCredentials(credentials);
        }
        Callable<Exception> callable = new Callable<Exception>() {
            @Override
            public Exception call() {
                try {
                    DatabaseUtils.testConnection(connectionConfig.getDriverClass(),
                        connectionConfig.getUrl(), credential.getUsername(),
                        credential.getPwd(), connectionConfig.isMysql8());
                } catch (ClassNotFoundException | SQLException e) {
                    return e;
                }
                return null;
            }
        };
        FutureTask<Exception> future = new FutureTask<>(callable);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(future, "Connect to Database", true, project);
        Exception exception;
        try {
            exception = future.get();
        } catch (InterruptedException | ExecutionException e) {
            Messages.showMessageDialog(project, "Failed to connect to database \n " + e.getMessage(), "Mybatis Generator Plus", Messages.getErrorIcon());
            return;
        }
        if (exception != null) {
            Messages.showMessageDialog(project, "Failed to connect to database \n " + exception.getMessage(), "Mybatis Generator Plus", Messages.getErrorIcon());
            return;
        }

        if (overrideBox.getSelectedObjects() != null) {
            int confirm = Messages.showOkCancelDialog(project, "The exists file will be overwrite ,Confirm generate?", "Mybatis Generator Plus", Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
        } else {
            int confirm = Messages.showOkCancelDialog(project, "Confirm generate mybatis code?", "Mybatis Generator Plus", Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
        }

        super.doOKAction();

        this.generate(connectionConfig);

    }

    /**
     * 获取选中的表的数据库连接
     * @return
     */
    private ConnectionConfig getRawConnectionConfig() {
        PsiElement psiElement = psiElements[0];
        DbDataSource dbDataSource = (DbDataSource) psiElement.getParent().getParent();
        DbNamespace dbNamespace = (DbNamespace) psiElement.getParent();


        if (dbDataSource == null) {
            Messages.showMessageDialog(project, "Cannot get datasource", "Mybatis Generator Plus", Messages.getErrorIcon());
            return null;
        }

        String schema = dbNamespace.getName();
        RawConnectionConfig connectionConfig = dbDataSource.getConnectionConfig();

        if (connectionConfig == null) {
            Messages.showMessageDialog(project, "Cannot get connection config", "Mybatis Generator Plus", Messages.getErrorIcon());
            return null;
        }
        ConnectionConfig config = new ConnectionConfig(connectionConfig.getName(),
            connectionConfig.getDriverClass(), connectionConfig.getUrl());
        NameVersion databaseVersion = dbDataSource.getDatabaseVersion();
        config.setDataBaseName(databaseVersion.name);
        config.setDataBaseVersion(databaseVersion.version);
        config.setSchema(schema);
        return config;
    }


    private boolean getDatabaseCredential(Credential credential) {
        DatabaseCredentialUI databaseCredentialUI = new DatabaseCredentialUI(anActionEvent.getProject(), credential);
        return databaseCredentialUI.showAndGet();
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(8, 4, 10, 10));
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

        useExampleBox.addChangeListener(e -> {
            exampleNamePanel.setVisible(
                mbgJavaClientTypeBox.isVisible() && useExampleBox.getSelectedObjects() != null);
            examplePackagePanel.setVisible(
                mbgJavaClientTypeBox.isVisible() && useExampleBox.getSelectedObjects() != null);
        });

        commentBox.setSelected(tableConfig.isComment());
        overrideBox.setSelected(tableConfig.isOverride());
        needToStringHashcodeEqualsBox.setSelected(tableConfig.isNeedToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(tableConfig.isUseSchemaPrefix());
        annotationDAOBox.setSelected(tableConfig.isAnnotationDAO());
        useDAOExtendStyleBox.setSelected(tableConfig.isUseDAOExtendStyle());
        jsr310SupportBox.setSelected(tableConfig.isJsr310Support());
        annotationBox.setSelected(tableConfig.isAnnotation());
        useActualColumnNamesBox.setSelected(tableConfig.isUseActualColumnNames());
        useTableNameAliasBox.setSelected(tableConfig.isUseTableNameAlias());
        useExampleBox.setSelected(tableConfig.isUseExample());
        lombokAnnotationBox.setSelected(tableConfig.isLombokAnnotation());
        lombokBuilderAnnotationBox.setSelected(tableConfig.isLombokBuilderAnnotation());
        optionsPanel.setName("Options");
        tabpanel.add(optionsPanel);
    }

    /**
     * 初始化Package组件
     */
    private void initHeader(String tableName, String primaryKey) {
        JPanel headerPanel = new JBPanel<>();
        headerPanel.setBorder(JBUI.Borders.empty(0, 5));
        VerticalFlowLayout layout = new VerticalFlowLayout(VerticalFlowLayout.TOP);
        layout.setVgap(0);
        headerPanel.setLayout(layout);
        JPanel moduleRootPanel = new JPanel();
        moduleRootPanel.setLayout(new BoxLayout(moduleRootPanel, BoxLayout.X_AXIS));
        JBLabel projectRootLabel = new JBLabel("Module Root:");
//        projectRootLabel.setPreferredSize(new Dimension(150, 10));
        moduleRootField.addBrowseFolderListener(new TextBrowseFolderListener(
            FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                //修改package选择器初始路径
                initPackageInitialPath(moduleRootField.getText());
            }
        });
        if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getModuleRootPath())) {
            //历史值
            moduleRootField.setText(tableConfig.getModuleRootPath());
        } else {
            moduleRootField.setText(project.getBasePath());
        }
        moduleRootPanel.add(projectRootLabel);
        moduleRootPanel.add(moduleRootField);

        //Table
        JPanel tableNamePanel = new JPanel();
        tableNamePanel.setLayout(new BoxLayout(tableNamePanel, BoxLayout.X_AXIS));
        JLabel tableLabel = new JLabel("Table Name:");
        tableLabel.setLabelFor(tableNameField);
//        tableLabel.setPreferredSize(new Dimension(150, 10));
        tableNamePanel.add(tableLabel);
        tableNamePanel.add(tableNameField);

        if (psiElements.length > 1) {
            tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg:db_table"));
        } else {
            tableNameField.setText(tableName);
        }
        tableNameField.setEditable(false);
        tableNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String domainName = StringUtils.dbStringToCamelStyle(tableNameField.getText());
                domainNameField.setText(domainName);
                mapperNameField.setText(getMapperName(domainName));
                exampleNameField.setText(getExampleName(domainName));
            }
        });

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.X_AXIS));
        JLabel primaryKeyLabel = new JLabel("   Primary Key:");
        primaryKeyLabel.setLabelFor(primaryKeyField);
//        primaryKeyLabel.setPreferredSize(new Dimension(150, 10));
        tableNamePanel.add(primaryKeyLabel);
        tableNamePanel.add(primaryKeyField);

        primaryKeyField.setText(primaryKey);
        primaryKeyField.setEditable(false);
        headerPanel.add(moduleRootPanel);
        headerPanel.add(tableNamePanel);
        headerPanel.add(primaryPanel);
        contentPane.add(headerPanel);
    }

    private void initPackageInitialPath(String moduleRootPath) {
        basePackageInitialPath = moduleRootPath + File.separator + tableConfig.getSourcePath();
        domainPackageInitialPath = moduleRootPath + File.separator + tableConfig.getSourcePath();
        mapperPackageInitialPath = moduleRootPath + File.separator + tableConfig.getSourcePath();
        examplePackageInitialPath = moduleRootPath + File.separator + tableConfig.getSourcePath();
        xmlPackageInitialPath = moduleRootPath + File.separator + tableConfig.getResourcePath();
    }

    private void initGeneralPanel(String domainName) {
        JPanel mgbTargetRuntimePanel = initMbgTargbgetRuntimePanel();
        JPanel domainNamePanel = initDomainNamePanel(domainName);
        JPanel mapperNamePanel = initMapperNamePanel(domainName);
        initExampleNamePanel(domainName);
        initPackageInitialPath(moduleRootField.getText());
        JPanel basePackagePanel = initPackagePanel("Base Package:",
            Objects.nonNull(tableConfig) ? tableConfig.getBasePackage() : "", basePackageField,
            PackageTypeEnum.BASE);
        JPanel domainPackagePanel = initPackagePanel("Domain Package:",
            Objects.nonNull(tableConfig) ? tableConfig.getDomainPackage() : "", domainPackageField,
            PackageTypeEnum.DOMAIN);
        JPanel mapperPackagePanel = initPackagePanel("Mapper Package:",
            Objects.nonNull(tableConfig) ? tableConfig.getMapperPackage() : "", mapperPackageField,
            PackageTypeEnum.MAPPER);
        examplePackagePanel = initPackagePanel("Example Package:",
            Objects.nonNull(tableConfig) ? tableConfig.getExamplePackage() : "",
            examplePackageField, PackageTypeEnum.EXAMPLE);
        examplePackagePanel.setVisible(
            mbgJavaClientTypeBox.isVisible() && useExampleBox.getSelectedObjects() != null);
        JPanel xmlPackagePanel = initPackagePanel("Xml Package:",
            Objects.nonNull(tableConfig) ? tableConfig.getXmlPackage() : "", xmlPackageField,
            PackageTypeEnum.XML);
        xmlPackageField.setVisible(mbgJavaClientTypeBox.isVisible());
        xmlPackagePanel.setVisible(mbgJavaClientTypeBox.isVisible());

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        // ******** Runtime & Type ui  *********
        generalPanel.add(new TitledSeparator("Runtime And Type"));
        JPanel runtimePanel = new JPanel();
        runtimePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        runtimePanel.add(mgbTargetRuntimePanel);
        generalPanel.add(runtimePanel);

        // ******** Domain ui  *********
        generalPanel.add(new TitledSeparator("Domain"));
        JPanel domainPanel = new JPanel();
        domainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        domainPanel.add(domainNamePanel);
        domainPanel.add(mapperNamePanel);
        domainPanel.add(exampleNamePanel);
        generalPanel.add(domainPanel);

        // ******** Package ui  *********
        generalPanel.add(new TitledSeparator("Package"));
        JPanel packagePanel = new JPanel();
        packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        packagePanel.add(basePackagePanel);
        packagePanel.add(domainPackagePanel);
        packagePanel.add(mapperPackagePanel);
        packagePanel.add(examplePackagePanel);
        packagePanel.add(xmlPackagePanel);
        generalPanel.add(packagePanel);

        generalPanel.setName("General");
        tabpanel.add(generalPanel);
    }


    @NotNull
    private JPanel initPackagePanel(String labelText, String historyFieldText,
        TextFieldWithBrowseButton textFieldWithBrowseButton, PackageTypeEnum packageType) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JBLabel label = new JBLabel(labelText);
//        label.setPreferredSize(new Dimension(150, 10));
        textFieldWithBrowseButton.setText(historyFieldText);
        textFieldWithBrowseButton.setEditable(true);
        textFieldWithBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(
            FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
            @Override
            protected VirtualFile getInitialFile() {
                String initialPath = "";
                switch (packageType) {
                    case BASE:
                        initialPath = basePackageInitialPath;
                        break;
                    case DOMAIN:
                        initialPath = domainPackageInitialPath;
                        break;
                    case MAPPER:
                        initialPath = mapperPackageInitialPath;
                        break;
                    case EXAMPLE:
                        initialPath = examplePackageInitialPath;
                        break;
                    case XML:
                        initialPath = xmlPackageInitialPath;
                        break;
                }
                VirtualFile virtualFile = LocalFileSystem.getInstance()
                    .findFileByPath(initialPath);
                return virtualFile;
            }

            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                //选择的绝对路径
                String choosedAbsolutePath = chosenFile.getPresentableUrl();
                //将项目根路径去掉,得到相对路径
                String relativePath = choosedAbsolutePath.replace(moduleRootField.getText(), "");
                //将源码文件路径(source path)和配置文件(resource path)路径去掉
                String packagePath = relativePath.replace(tableConfig.getSourcePath(), "")
                    .replace(tableConfig.getResourcePath(), "").replaceAll(File.separator, ".");
                if (packagePath.startsWith("..")) {
                    packagePath = packagePath.substring(2, packagePath.length());
                } else {
                    packagePath = "";
                }
                switch (packageType) {
                    case BASE:
                        if (org.apache.commons.lang3.StringUtils.isBlank(packagePath)) {
                            domainPackageField.setText("entity");
                            mapperPackageField.setText("mapper");
                            if (examplePackageField.isVisible()) {
                                examplePackageField.setText("example");
                            }
                        }else {
                            domainPackageField.setText(packagePath + ".entity");
                            mapperPackageField.setText(packagePath + ".mapper");
                            if (examplePackageField.isVisible()) {
                                examplePackageField.setText(packagePath + ".example");
                            }
                        }
                        xmlPackageField.setText(tableConfig.getXmlPackage());
                        basePackageInitialPath = choosedAbsolutePath;
                        domainPackageInitialPath = choosedAbsolutePath;
                        mapperPackageInitialPath = choosedAbsolutePath;
                        examplePackageInitialPath = choosedAbsolutePath;
                        break;
                    case DOMAIN:
                        domainPackageInitialPath = choosedAbsolutePath;
                        break;
                    case MAPPER:
                        mapperPackageInitialPath = choosedAbsolutePath;
                        break;
                    case EXAMPLE:
                        examplePackageInitialPath = choosedAbsolutePath;
                        break;
                    case XML:
                        xmlPackageInitialPath = choosedAbsolutePath;
                        if (org.apache.commons.lang3.StringUtils.isBlank(packagePath)) {
                            packagePath = tableConfig.getXmlPackage();
                        }
                        break;
                }
                return packagePath;
            }
        });
        panel.add(label);
        panel.add(textFieldWithBrowseButton);
        return panel;
    }

    private void initExampleNamePanel(String domainName) {
        exampleNamePanel.setLayout(new BoxLayout(exampleNamePanel, BoxLayout.X_AXIS));
        JLabel exampleNameLabel = new JLabel("Example Name:");
//        exampleNameLabel.setPreferredSize(new Dimension(150, 10));
        exampleNameLabel.setLabelFor(domainNameField);
        exampleNamePanel.add(exampleNameLabel);
        exampleNamePanel.add(exampleNameField);
        if (psiElements.length > 1) {
            if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getExamplePostfix())) {
                exampleNameField.addFocusListener(new JTextFieldHintListener(exampleNameField, "eg:DbTable" + tableConfig.getExamplePostfix()));
            } else {
                exampleNameField.addFocusListener(new JTextFieldHintListener(exampleNameField, "eg:DbTable" + "Example"));
            }
        } else {
            exampleNameField.setText(getExampleName(domainName));
        }
        exampleNamePanel.setVisible(mbgJavaClientTypeBox.isVisible() && tableConfig.isUseExample());
    }

    @NotNull
    private JPanel initMapperNamePanel(String domainName) {
        //MapperName
        JPanel mapperNamePanel = new JPanel();
        mapperNamePanel.setLayout(new BoxLayout(mapperNamePanel, BoxLayout.X_AXIS));
        JLabel mapperNameLabel = new JLabel("Mapper Name:");
//        mapperNameLabel.setPreferredSize(new Dimension(150, 10));
        mapperNameLabel.setLabelFor(domainNameField);
        mapperNamePanel.add(mapperNameLabel);
        mapperNamePanel.add(mapperNameField);
        if (psiElements.length > 1) {
            if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getMapperPostfix())) {
                mapperNameField.addFocusListener(new JTextFieldHintListener(mapperNameField, "eg:DbTable" + tableConfig.getMapperPostfix()));
            } else {
                mapperNameField.addFocusListener(new JTextFieldHintListener(mapperNameField, "eg:DbTable" + "Mapper"));
            }
        } else {
            mapperNameField.setText(getMapperName(domainName));
        }
        return mapperNamePanel;
    }

    @NotNull
    private JPanel initDomainNamePanel(String domainName) {
        //domainName
        JPanel domainNamePanel = new JPanel();
        domainNamePanel.setLayout(new BoxLayout(domainNamePanel, BoxLayout.X_AXIS));
        JLabel entityNameLabel = new JLabel("Domain Name:");
//        entityNameLabel.setPreferredSize(new Dimension(150, 10));
        domainNamePanel.add(entityNameLabel);
        domainNamePanel.add(domainNameField);
        if (psiElements.length > 1) {
            if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getDomainPostfix())){
                domainNameField.addFocusListener(new JTextFieldHintListener(domainNameField,
                    "eg:DbTable" + tableConfig.getDomainPostfix()));
            }else {
                domainNameField.addFocusListener(
                    new JTextFieldHintListener(domainNameField, "eg:DbTable" + "Entity"));
            }
        } else {
            domainNameField.setText(domainName + "Entity");
        }
        domainNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                mapperNameField.setText(getMapperName(domainNameField.getText()));
                exampleNameField.setText(getExampleName(domainNameField.getText()));
            }
        });
        return domainNamePanel;
    }

    @NotNull
    private JPanel initMbgTargbgetRuntimePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 2, 160, 0));

        // mbgTargetRuntime
        JPanel runtimePanel = new JPanel();
        runtimePanel.setLayout(new BoxLayout(runtimePanel, BoxLayout.X_AXIS));
        JLabel runtimeLabel = new JLabel("Target Runtime:");
        runtimePanel.add(runtimeLabel);
        for (MbgTargetRuntimeEnum value : MbgTargetRuntimeEnum.values()) {
            mbgTargetRuntimeBox.addItem(value.getName());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(tableConfig.getMgbTargetRuntime())) {
            mbgTargetRuntimeBox.setSelectedItem(tableConfig.getMgbTargetRuntime());
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
                    exampleNamePanel.setVisible(mbgJavaClientTypeBox.isVisible());
                    examplePackagePanel.setVisible(mbgJavaClientTypeBox.isVisible());
                    xmlPackageField.getParent().setVisible(mbgJavaClientTypeBox.isVisible());
                    xmlPackageField.setVisible(mbgJavaClientTypeBox.isVisible());
                    useExampleBox.setVisible(mbgJavaClientTypeBox.isVisible());
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
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tableConfig.getMgbJavaClientConfigType())) {
                mbgJavaClientTypeBox.setSelectedItem(tableConfig.getMgbJavaClientConfigType());
            }else {
                mbgJavaClientTypeBox.setSelectedIndex(0);
            }
        }else {
            typePanel.setVisible(false);
            mbgJavaClientTypeBox.setVisible(false);
        }
        typePanel.add(mbgJavaClientTypeBox);
        jPanel.add(typePanel);

        return jPanel;
    }

    public void generate(ConnectionConfig connectionConfig) {
        tableConfig.setName(tableNameField.getText());
        tableConfig.setTableName(tableNameField.getText());
        tableConfig.setModuleRootPath(moduleRootField.getText());

        tableConfig.setBasePackage(basePackageField.getText());
        tableConfig.setDomainPackage(domainPackageField.getText());
        tableConfig.setMapperPackage(mapperPackageField.getText());
        tableConfig.setExamplePackage(examplePackageField.getText());
        tableConfig.setXmlPackage(xmlPackageField.getText());

        tableConfig.setMapperName(mapperNameField.getText());
        tableConfig.setDomainName(domainNameField.getText());
        tableConfig.setPrimaryKey(primaryKeyField.getText());
        tableConfig.setExampleName(exampleNameField.getText());

        tableConfig.setMgbTargetRuntime((String) mbgTargetRuntimeBox.getSelectedItem());
        tableConfig.setMgbJavaClientConfigType((String) mbgJavaClientTypeBox.getSelectedItem());

        tableConfig.setComment(commentBox.getSelectedObjects() != null);
        tableConfig.setOverride(overrideBox.getSelectedObjects() != null);
        tableConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        tableConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        tableConfig.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
        tableConfig.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
        tableConfig.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
        tableConfig.setAnnotation(annotationBox.getSelectedObjects() != null);
        tableConfig.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
        tableConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        tableConfig.setUseExample(useExampleBox.getSelectedObjects() != null);
        tableConfig.setMysql8(connectionConfig.isMysql8());
        tableConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
        tableConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);
        tableConfig.setSourcePath(this.tableConfig.getSourcePath());
        tableConfig.setResourcePath(this.tableConfig.getResourcePath());

        new MyBatisGenerateCommand(tableConfig).execute(project, connectionConfig);

    }

    private String getMapperName(String domainName) {
        if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getMapperPostfix())) {
            return domainName + tableConfig.getMapperPostfix();
        } else {
            return (domainName + "Mapper");
        }
    }

    private String getExampleName(String entityName) {
        if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getExamplePostfix())) {
            return entityName + tableConfig.getExamplePostfix();
        } else {
            return (entityName + "Example");
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
