package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.contants.PluginContants;
import com.github.leecho.idea.plugin.mybatis.generator.generate.MyBatisGenerateCommand;
import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableInfo;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.github.leecho.idea.plugin.mybatis.generator.util.DatabaseUtils;
import com.github.leecho.idea.plugin.mybatis.generator.util.JTextFieldHintListener;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
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

	private JButton columnSettingButton = new JButton("Column Setting");
	private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton basePackageField = new TextFieldWithBrowseButton();
	private EditorTextFieldWithBrowseButton domainPackageField;
	private EditorTextFieldWithBrowseButton mapperPackageField;
	private EditorTextFieldWithBrowseButton examplePackageField;
	private JTextField xmlPackageField = new JTextField();
	private JTextField mapperNameField = new JBTextField(20);
	private JTextField domainNameField = new JBTextField(20);
	private JTextField exampleNameField = new JBTextField(20);
	private JTextField primaryKeyField = new JBTextField(20);

	private JPanel examplePackagePanel = new JPanel();
	private JPanel exampleNamePanel = new JPanel();

	private JLabel connectStatusLabel = new JLabel();

	private JCheckBox offsetLimitBox = new JCheckBox("Page(分页)");
	private JCheckBox commentBox = new JCheckBox("comment(实体注释)");
	private JCheckBox overrideBox = new JCheckBox("Overwrite");
	private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
	private JCheckBox useSchemaPrefixBox = new JCheckBox("Use-Schema(使用Schema前缀)");
	private JCheckBox needForUpdateBox = new JCheckBox("Add-ForUpdate(select增加ForUpdate)");
	private JCheckBox annotationDAOBox = new JCheckBox("Repository-Annotation(Repository注解)");
	private JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent-Interface(公共父接口)");
	private JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
	private JCheckBox annotationBox = new JCheckBox("JPA-Annotation(JPA注解)");
	private JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column(实际的列名)");
	private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias(启用别名查询)");
	private JCheckBox useExampleBox = new JCheckBox("Use-Example");
	private JCheckBox mysql8Box = new JCheckBox("mysql_8");
	private JCheckBox lombokAnnotationBox = new JCheckBox("Lombok");
	private JCheckBox lombokBuilderAnnotationBox = new JCheckBox("Lombok Builder");


	public GenerateSettingUI(AnActionEvent anActionEvent) {
		super(anActionEvent.getData(PlatformDataKeys.PROJECT));
		Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

		this.anActionEvent = anActionEvent;
		this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
		this.myBatisGeneratorConfiguration = MyBatisGeneratorConfiguration.getInstance(project);
		this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

		GlobalConfig globalConfig = myBatisGeneratorConfiguration.getGlobalConfig();
		Map<String, TableConfig> historyConfigList = myBatisGeneratorConfiguration.getTableConfigs();

		setModal(true);
		setTitle("Generate Setting");
		//设置大小
		pack();
		setModal(true);

		PsiElement psiElement = psiElements[0];
		TableInfo tableInfo = new TableInfo((DbTable) psiElement);
		String tableName = tableInfo.getTableName();
		String entityName = StringUtils.dbStringToCamelStyle(tableName);
		String primaryKey = "";
		if (tableInfo.getPrimaryKeys().size() > 0) {
			primaryKey = tableInfo.getPrimaryKeys().get(0);
		}

		//单表时，优先使用已经存在的配置
		if (historyConfigList != null) {
			tableConfig = historyConfigList.get(tableName);
		}
		if (tableConfig == null) {
			//初始化配置
			tableConfig = new TableConfig();
			tableConfig.setModuleRootPath(globalConfig.getModuleRootPath());
			tableConfig.setSourcePath(globalConfig.getSourcePath());
			tableConfig.setResourcePath(globalConfig.getResourcePath());
			tableConfig.setDomainPackage(globalConfig.getDomainPackage());
			tableConfig.setMapperPackage(globalConfig.getMapperPackage());
			tableConfig.setMapperPostfix(globalConfig.getMapperPostfix());
			tableConfig.setExamplePostfix(globalConfig.getExamplePostfix());
			tableConfig.setExamplePackage(globalConfig.getExamplePackage());
			tableConfig.setXmlPackage(globalConfig.getXmlPackage());

			tableConfig.setOffsetLimit(globalConfig.isOffsetLimit());
			tableConfig.setComment(globalConfig.isComment());
			tableConfig.setOverride(globalConfig.isOverride());
			tableConfig.setNeedToStringHashcodeEquals(globalConfig.isNeedToStringHashcodeEquals());
			tableConfig.setUseSchemaPrefix(globalConfig.isUseSchemaPrefix());
			tableConfig.setNeedForUpdate(globalConfig.isNeedForUpdate());
			tableConfig.setAnnotationDAO(globalConfig.isAnnotationDAO());
			tableConfig.setUseDAOExtendStyle(globalConfig.isUseDAOExtendStyle());
			tableConfig.setJsr310Support(globalConfig.isJsr310Support());
			tableConfig.setAnnotation(globalConfig.isAnnotation());
			tableConfig.setUseActualColumnNames(globalConfig.isUseActualColumnNames());
			tableConfig.setUseTableNameAlias(globalConfig.isUseTableNameAlias());
			tableConfig.setUseExample(globalConfig.isUseExample());
			tableConfig.setMysql8(globalConfig.isMysql8());
			tableConfig.setLombokAnnotation(globalConfig.isLombokAnnotation());
			tableConfig.setLombokBuilderAnnotation(globalConfig.isLombokBuilderAnnotation());
			tableConfig.setPrimaryKey(primaryKey);
		}

		contentPane.setPreferredSize(new Dimension(700, 500));
		contentPane.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

		//initDatabasePanel();
		this.initDomainPanel(tableName, entityName, primaryKey);
		//Model
		this.initPackagePanel();
		this.initOptionsPanel();

		contentPane.add(connectStatusLabel);

		this.init();
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

		RawConnectionConfig connectionConfig = ((DbDataSource) psiElements[0].getParent().getParent()).getConnectionConfig();
		Map<String, Credential> credentials = myBatisGeneratorConfiguration.getCredentials();
		Credential credential;
		if (credentials == null || !credentials.containsKey(connectionConfig.getUrl())) {
			boolean result = getDatabaseCredential(connectionConfig);
			if (result) {
				credentials = myBatisGeneratorConfiguration.getCredentials();
				credential = credentials.get(connectionConfig.getUrl());
			} else {
				return;
			}
		} else {
			credential = credentials.get(connectionConfig.getUrl());
		}

		if (!this.testConnection(connectionConfig, credential)) {
			return;
		}

		if (overrideBox.getSelectedObjects() != null) {
			int confirm = Messages.showOkCancelDialog(project, "The exists file will be overwrite ,Confirm start generate?", "Mybatis Generator Plus", Messages.getQuestionIcon());
			if (confirm == 2) {
				return;
			}
		} else {
			int confirm = Messages.showOkCancelDialog(project, "Confirm start generate?", "Mybatis Generator Plus", Messages.getQuestionIcon());
			if (confirm == 2) {
				return;
			}
		}

		super.doOKAction();

		this.generate(connectionConfig);

	}

	private boolean testConnection(RawConnectionConfig connectionConfig, Credential credential) {
		String url = connectionConfig.getUrl();
		CredentialAttributes credentialAttributes = new CredentialAttributes(PluginContants.PLUGIN_NAME + "-" + url, credential.getUsername(), this.getClass(), false);
		String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
		try {
			connectStatusLabel.setText("Connecting Database");
			DatabaseUtils.testConnection(connectionConfig.getDriverClass(), connectionConfig.getUrl(), credential.getUsername(), password, mysql8Box.getSelectedObjects() != null);
			connectStatusLabel.setText("Connect Database Successfully");
			return true;
		} catch (ClassNotFoundException e) {
			connectStatusLabel.setText("Connect Database Failed");
			Messages.showMessageDialog(project, "Failed to connect to database \n " + e.getMessage(), "Test Connection", Messages.getErrorIcon());
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			connectStatusLabel.setText("Connect Database Failed");
			Messages.showMessageDialog(project, "Failed to connect to database \n " + e.getMessage(), "Test Connection", Messages.getErrorIcon());
			if (e.getErrorCode() == 1045) {
				boolean result = getDatabaseCredential(connectionConfig);
				if (result) {
					Map<String, Credential> credentials = myBatisGeneratorConfiguration.getCredentials();
					return testConnection(connectionConfig, credentials.get(connectionConfig.getUrl()));
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	private boolean getDatabaseCredential(RawConnectionConfig connectionConfig) {
		DatabaseCredentialUI databaseCredentialUI = new DatabaseCredentialUI(anActionEvent.getProject(), connectionConfig.getUrl());
		return databaseCredentialUI.showAndGet();
	}

	private void initOptionsPanel() {
		JBPanel optionsPanel = new JBPanel(new GridLayout(8, 2, 10, 10));
		TitledSeparator separator = new TitledSeparator();
		separator.setText("Options");
		separator.setLabelFor(optionsPanel);
		contentPane.add(separator);

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

		useExampleBox.addChangeListener(e -> {
			exampleNamePanel.setVisible(useExampleBox.getSelectedObjects() != null);
			examplePackagePanel.setVisible(useExampleBox.getSelectedObjects() != null);
		});

		offsetLimitBox.setSelected(tableConfig.isOffsetLimit());
		commentBox.setSelected(tableConfig.isComment());
		overrideBox.setSelected(tableConfig.isOverride());
		needToStringHashcodeEqualsBox.setSelected(tableConfig.isNeedToStringHashcodeEquals());
		useSchemaPrefixBox.setSelected(tableConfig.isUseSchemaPrefix());
		needForUpdateBox.setSelected(tableConfig.isNeedForUpdate());
		annotationDAOBox.setSelected(tableConfig.isAnnotationDAO());
		useDAOExtendStyleBox.setSelected(tableConfig.isUseDAOExtendStyle());
		jsr310SupportBox.setSelected(tableConfig.isJsr310Support());
		annotationBox.setSelected(tableConfig.isAnnotation());
		useActualColumnNamesBox.setSelected(tableConfig.isUseActualColumnNames());
		useTableNameAliasBox.setSelected(tableConfig.isUseTableNameAlias());
		useExampleBox.setSelected(tableConfig.isUseExample());
		mysql8Box.setSelected(tableConfig.isMysql8());
		lombokAnnotationBox.setSelected(tableConfig.isLombokAnnotation());
		lombokBuilderAnnotationBox.setSelected(tableConfig.isLombokBuilderAnnotation());
		contentPane.add(optionsPanel);
	}

	/**
	 * 初始化Package组件
	 */
	private void initPackagePanel() {

		JPanel moduleRootPanel = new JPanel();
		moduleRootPanel.setLayout(new BoxLayout(moduleRootPanel, BoxLayout.X_AXIS));
		JBLabel projectRootLabel = new JBLabel("Module Root:");
		projectRootLabel.setPreferredSize(new Dimension(150, 20));
		moduleRootField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				moduleRootField.setText(moduleRootField.getText().replaceAll("\\\\", "/"));
			}
		});
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getModuleRootPath())) {
			moduleRootField.setText(tableConfig.getModuleRootPath());
		} else {
			moduleRootField.setText(project.getBasePath());
		}
		moduleRootPanel.add(projectRootLabel);
		moduleRootPanel.add(moduleRootField);

		JPanel basePackagePanel = new JPanel();
		basePackagePanel.setLayout(new BoxLayout(basePackagePanel, BoxLayout.X_AXIS));
		JBLabel basePackageLabel = new JBLabel("Base Package:");
		basePackageLabel.setPreferredSize(new Dimension(150, 20));
		basePackageField.setEditable(false);
		basePackageField.addActionListener(e -> {
			final PackageChooserDialog chooser = new PackageChooserDialog("Select Base Package", project);
			chooser.selectPackage(basePackageField.getText());
			chooser.show();
			final PsiPackage psiPackage = chooser.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			basePackageField.setText(packageName);
			domainPackageField.setText(packageName + ".domain");
			mapperPackageField.setText(packageName + "." + getMapperPostfix().toLowerCase());
			examplePackageField.setText(packageName + "." + getExamplePostfix().toLowerCase());
		});
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getBasePackage())) {
			basePackageField.setText(tableConfig.getBasePackage());
		} else {
			basePackageField.setText("");
		}
		basePackagePanel.add(basePackageLabel);
		basePackagePanel.add(basePackageField);

		this.domainPackageField = new EditorTextFieldWithBrowseButton(project, false);


		JPanel entityPackagePanel = new JPanel();
		entityPackagePanel.setLayout(new BoxLayout(entityPackagePanel, BoxLayout.X_AXIS));
		JBLabel entityPackageLabel = new JBLabel("Domain Package:");
		entityPackageLabel.setPreferredSize(new Dimension(150, 20));
		domainPackageField.addActionListener(e -> {
			final PackageChooserDialog chooser = new PackageChooserDialog("Select Entity Package", project);
			chooser.selectPackage(domainPackageField.getText());
			chooser.show();
			final PsiPackage psiPackage = chooser.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			domainPackageField.setText(packageName);
		});
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getDomainPackage())) {
			domainPackageField.setText(tableConfig.getDomainPackage());
		} else {
			domainPackageField.setText("");
		}
		entityPackagePanel.add(entityPackageLabel);
		entityPackagePanel.add(domainPackageField);

		JPanel mapperPackagePanel = new JPanel();
		mapperPackagePanel.setLayout(new BoxLayout(mapperPackagePanel, BoxLayout.X_AXIS));
		JLabel mapperPackageLabel = new JLabel("Mapper Package:");
		mapperPackageLabel.setPreferredSize(new Dimension(150, 20));
		mapperPackageField = new EditorTextFieldWithBrowseButton(project, false);
		mapperPackageField.addActionListener(event -> {
			final PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Mapper Package", project);
			packageChooserDialog.selectPackage(mapperPackageField.getText());
			packageChooserDialog.show();

			final PsiPackage psiPackage = packageChooserDialog.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			mapperPackageField.setText(packageName);
		});
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getMapperPackage())) {
			mapperPackageField.setText(tableConfig.getMapperPackage());
		} else {
			mapperPackageField.setText("");
		}
		mapperPackagePanel.add(mapperPackageLabel);
		mapperPackagePanel.add(mapperPackageField);

		examplePackagePanel.setLayout(new BoxLayout(examplePackagePanel, BoxLayout.X_AXIS));

		examplePackageField = new EditorTextFieldWithBrowseButton(project, false);
		examplePackageField.addActionListener(e -> {
			final PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Example Package", project);
			packageChooserDialog.selectPackage(examplePackageField.getText());
			packageChooserDialog.show();

			final PsiPackage psiPackage = packageChooserDialog.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			examplePackageField.setText(packageName);
		});

		JLabel examplePackageLabel = new JLabel("Example Package:");
		examplePackageLabel.setPreferredSize(new Dimension(150, 20));
		examplePackageField.setText(tableConfig.getExamplePackage());
		examplePackagePanel.add(examplePackageLabel);
		examplePackagePanel.add(examplePackageField);
		examplePackagePanel.setVisible(tableConfig.isUseExample());

		JPanel xmlPackagePanel = new JPanel();
		xmlPackagePanel.setLayout(new BoxLayout(xmlPackagePanel, BoxLayout.X_AXIS));
		JLabel xmlPackageLabel = new JLabel("Xml Package:");
		xmlPackageLabel.setPreferredSize(new Dimension(150, 20));
		xmlPackageField.setText(tableConfig.getXmlPackage());
		xmlPackagePanel.add(xmlPackageLabel);
		xmlPackagePanel.add(xmlPackageField);

		JPanel packagePanel = new JPanel();
		packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		packagePanel.add(moduleRootPanel);
		packagePanel.add(basePackagePanel);
		packagePanel.add(entityPackagePanel);
		packagePanel.add(mapperPackagePanel);
		packagePanel.add(examplePackagePanel);
		packagePanel.add(xmlPackagePanel);

		TitledSeparator separator = new TitledSeparator();
		separator.setText("Package");
		contentPane.add(separator);
		contentPane.add(packagePanel);
	}

	private void initDomainPanel(String tableName, String modelName, String primaryKey) {
		JPanel entityPanel = new JPanel();
		entityPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		TitledSeparator separator = new TitledSeparator();
		separator.setText("Domain");
		contentPane.add(separator);
		contentPane.add(entityPanel);

		//Table
		JPanel tableNamePanel = new JPanel();
		tableNamePanel.setLayout(new BoxLayout(tableNamePanel, BoxLayout.X_AXIS));
		JLabel tableLabel = new JLabel("Table Name:");
		tableLabel.setLabelFor(tableNameField);
		tableLabel.setPreferredSize(new Dimension(150, 20));
		tableNamePanel.add(tableLabel);
		tableNamePanel.add(tableNameField);
		tableNamePanel.add(columnSettingButton);

		PsiElement psiElement = psiElements[0];
		TableInfo tableInfo = new TableInfo((DbTable) psiElement);
		columnSettingButton.addActionListener(e -> {
			ColumnSettingUI columnSettingUI = new ColumnSettingUI(anActionEvent.getProject(), tableConfig, tableInfo);
			columnSettingUI.showAndGet();
		});
		if (psiElements.length > 1) {
			tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg:db_table"));
		} else {
			tableNameField.setText(tableName);
		}
		tableNameField.setEditable(false);
		tableNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String entityName = StringUtils.dbStringToCamelStyle(tableNameField.getText());
				domainNameField.setText(entityName);
				mapperNameField.setText(getMapperName(entityName));
				exampleNameField.setText(getExampleName(entityName));
			}
		});

		JPanel primaryPanel = new JPanel();
		primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.X_AXIS));
		JLabel primaryKeyLabel = new JLabel("Primary Key:");
		primaryKeyLabel.setLabelFor(primaryKeyField);
		primaryKeyLabel.setPreferredSize(new Dimension(150, 20));
		primaryPanel.add(primaryKeyLabel);
		primaryPanel.add(primaryKeyField);

		primaryKeyField.setText(primaryKey);
		primaryKeyField.setEditable(false);

		JPanel domainNamePanel = new JPanel();
		domainNamePanel.setLayout(new BoxLayout(domainNamePanel, BoxLayout.X_AXIS));
		JLabel entityNameLabel = new JLabel("Domain Name:");
		entityNameLabel.setPreferredSize(new Dimension(150, 20));
		domainNamePanel.add(entityNameLabel);
		domainNamePanel.add(domainNameField);
		entityPanel.add(domainNamePanel);
		if (psiElements.length > 1) {
			domainNameField.addFocusListener(new JTextFieldHintListener(domainNameField, "eg:DbTable"));
		} else {
			domainNameField.setText(modelName);
		}
		domainNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				mapperNameField.setText(getMapperName(domainNameField.getText()));
				exampleNameField.setText(getExampleName(domainNameField.getText()));
			}
		});

		//MapperName
		JPanel mapperNamePanel = new JPanel();
		mapperNamePanel.setLayout(new BoxLayout(mapperNamePanel, BoxLayout.X_AXIS));
		JLabel mapperNameLabel = new JLabel("Mapper Name:");
		mapperNameLabel.setPreferredSize(new Dimension(150, 20));
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
			mapperNameField.setText(getMapperName(modelName));
		}

		exampleNamePanel.setLayout(new BoxLayout(exampleNamePanel, BoxLayout.X_AXIS));
		JLabel exampleNameLabel = new JLabel("Example Name:");
		exampleNameLabel.setPreferredSize(new Dimension(150, 20));
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
			exampleNameField.setText(getExampleName(modelName));
		}

		exampleNamePanel.setVisible(tableConfig.isUseExample());

		entityPanel.add(tableNamePanel);
		entityPanel.add(primaryPanel);
		entityPanel.add(domainNamePanel);
		entityPanel.add(mapperNamePanel);
		entityPanel.add(exampleNamePanel);
	}

	public void generate(RawConnectionConfig connectionConfig) {
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

		tableConfig.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
		tableConfig.setComment(commentBox.getSelectedObjects() != null);
		tableConfig.setOverride(overrideBox.getSelectedObjects() != null);
		tableConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
		tableConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
		tableConfig.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
		tableConfig.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
		tableConfig.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
		tableConfig.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
		tableConfig.setAnnotation(annotationBox.getSelectedObjects() != null);
		tableConfig.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
		tableConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
		tableConfig.setUseExample(useExampleBox.getSelectedObjects() != null);
		tableConfig.setMysql8(mysql8Box.getSelectedObjects() != null);
		tableConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
		tableConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);

		tableConfig.setSourcePath(this.tableConfig.getSourcePath());
		tableConfig.setResourcePath(this.tableConfig.getResourcePath());

		new MyBatisGenerateCommand(tableConfig).execute(project, connectionConfig);

	}

	private String getMapperName(String entityName) {
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getMapperPostfix())) {
			return entityName + tableConfig.getMapperPostfix();
		} else {
			return (entityName + "Mapper");
		}
	}

	private String getMapperPostfix() {
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getMapperPostfix())) {
			return tableConfig.getMapperPostfix();
		} else {
			return "Mapper";
		}
	}

	private String getExamplePostfix() {
		if (tableConfig != null && !StringUtils.isEmpty(tableConfig.getExamplePostfix())) {
			return tableConfig.getExamplePostfix();
		} else {
			return "Example";
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
