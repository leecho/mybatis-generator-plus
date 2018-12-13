package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.contants.PluginContants;
import com.github.leecho.idea.plugin.mybatis.generator.generate.MyBatisGenerateCommand;
import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.github.leecho.idea.plugin.mybatis.generator.model.EntityConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
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
import com.intellij.ui.SeparatorWithText;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
	private EntityConfig entityConfig;

	private JPanel contentPane = new JBPanel<>();

	private JTextField tableNameField = new JBTextField(20);
	private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton basePackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton entityPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton mapperPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton examplePackageField = new TextFieldWithBrowseButton();
	private JTextField xmlPackageField = new JTextField();
	private JTextField mapperNameField = new JBTextField(20);
	private JTextField entityNameField = new JBTextField(20);
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
		Map<String, EntityConfig> historyConfigList = myBatisGeneratorConfiguration.getEntityConfigs();

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
			entityConfig = historyConfigList.get(tableName);
		}
		if (entityConfig == null) {
			//初始化配置
			entityConfig = new EntityConfig();
			entityConfig.setModuleRootPath(globalConfig.getModuleRootPath());
			entityConfig.setSourcePath(globalConfig.getSourcePath());
			entityConfig.setResourcePath(globalConfig.getResourcePath());
			entityConfig.setEntityPackage(globalConfig.getEntityPackage());
			entityConfig.setMapperPackage(globalConfig.getMapperPackage());
			entityConfig.setMapperPostfix(globalConfig.getMapperPostfix());
			entityConfig.setExamplePostfix(globalConfig.getExamplePostfix());
			entityConfig.setExamplePackage(globalConfig.getExamplePackage());
			entityConfig.setXmlPackage(globalConfig.getXmlPackage());

			entityConfig.setOffsetLimit(globalConfig.isOffsetLimit());
			entityConfig.setComment(globalConfig.isComment());
			entityConfig.setOverride(globalConfig.isOverride());
			entityConfig.setNeedToStringHashcodeEquals(globalConfig.isNeedToStringHashcodeEquals());
			entityConfig.setUseSchemaPrefix(globalConfig.isUseSchemaPrefix());
			entityConfig.setNeedForUpdate(globalConfig.isNeedForUpdate());
			entityConfig.setAnnotationDAO(globalConfig.isAnnotationDAO());
			entityConfig.setUseDAOExtendStyle(globalConfig.isUseDAOExtendStyle());
			entityConfig.setJsr310Support(globalConfig.isJsr310Support());
			entityConfig.setAnnotation(globalConfig.isAnnotation());
			entityConfig.setUseActualColumnNames(globalConfig.isUseActualColumnNames());
			entityConfig.setUseTableNameAlias(globalConfig.isUseTableNameAlias());
			entityConfig.setUseExample(globalConfig.isUseExample());
			entityConfig.setMysql8(globalConfig.isMysql8());
			entityConfig.setLombokAnnotation(globalConfig.isLombokAnnotation());
			entityConfig.setLombokBuilderAnnotation(globalConfig.isLombokBuilderAnnotation());
		}

		contentPane.setPreferredSize(new Dimension(600, 500));
		contentPane.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

		//initDatabasePanel();
		this.initEntityPanel(tableName, entityName, primaryKey);
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

		if (StringUtils.isEmpty(entityNameField.getText())) {
			errors.add("Entity name must not be null");
		}

		if (StringUtils.isEmpty(mapperNameField.getText())) {
			errors.add("Mapper name must not be null");
		}

		if (StringUtils.isEmpty(entityPackageField.getText())) {
			errors.add("Entity package must not be null");
		}

		if (StringUtils.isEmpty(mapperPackageField.getText())) {
			errors.add("Mapper package must not be null");
		}

		if (StringUtils.isEmpty(xmlPackageField.getText())) {
			errors.add("Mapper xml package must not be null");
		}

		if(useExampleBox.getSelectedObjects() != null){
			if(StringUtils.isEmpty(exampleNameField.getText())){
				errors.add("Example name must not be null");
			}
			if(StringUtils.isEmpty(examplePackageField.getText())){
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
		}else{
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

		offsetLimitBox.setSelected(entityConfig.isOffsetLimit());
		commentBox.setSelected(entityConfig.isComment());
		overrideBox.setSelected(entityConfig.isOverride());
		needToStringHashcodeEqualsBox.setSelected(entityConfig.isNeedToStringHashcodeEquals());
		useSchemaPrefixBox.setSelected(entityConfig.isUseSchemaPrefix());
		needForUpdateBox.setSelected(entityConfig.isNeedForUpdate());
		annotationDAOBox.setSelected(entityConfig.isAnnotationDAO());
		useDAOExtendStyleBox.setSelected(entityConfig.isUseDAOExtendStyle());
		jsr310SupportBox.setSelected(entityConfig.isJsr310Support());
		annotationBox.setSelected(entityConfig.isAnnotation());
		useActualColumnNamesBox.setSelected(entityConfig.isUseActualColumnNames());
		useTableNameAliasBox.setSelected(entityConfig.isUseTableNameAlias());
		useExampleBox.setSelected(entityConfig.isUseExample());
		mysql8Box.setSelected(entityConfig.isMysql8());
		lombokAnnotationBox.setSelected(entityConfig.isLombokAnnotation());
		lombokBuilderAnnotationBox.setSelected(entityConfig.isLombokBuilderAnnotation());

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
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getModuleRootPath())) {
			moduleRootField.setText(entityConfig.getModuleRootPath());
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
			entityPackageField.setText(packageName + ".domain");
			mapperPackageField.setText(packageName + "." + getMapperPostfix().toLowerCase());
			examplePackageField.setText(packageName + "." + getExamplePostfix().toLowerCase());
		});
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getBasePackage())) {
			basePackageField.setText(entityConfig.getBasePackage());
		} else {
			basePackageField.setText("");
		}
		basePackagePanel.add(basePackageLabel);
		basePackagePanel.add(basePackageField);

		JPanel entityPackagePanel = new JPanel();
		entityPackagePanel.setLayout(new BoxLayout(entityPackagePanel, BoxLayout.X_AXIS));
		JBLabel entityPackageLabel = new JBLabel("Entity Package:");
		entityPackageLabel.setPreferredSize(new Dimension(150, 20));
		entityPackageField.addActionListener(e -> {
			final PackageChooserDialog chooser = new PackageChooserDialog("Select Entity Package", project);
			chooser.selectPackage(entityPackageField.getText());
			chooser.show();
			final PsiPackage psiPackage = chooser.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			entityPackageField.setText(packageName);
		});
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getEntityPackage())) {
			entityPackageField.setText(entityConfig.getEntityPackage());
		} else {
			entityPackageField.setText("");
		}
		entityPackagePanel.add(entityPackageLabel);
		entityPackagePanel.add(entityPackageField);

		JPanel mapperPackagePanel = new JPanel();
		mapperPackagePanel.setLayout(new BoxLayout(mapperPackagePanel, BoxLayout.X_AXIS));
		JLabel mapperPackageLabel = new JLabel("Mapper Package:");
		mapperPackageLabel.setPreferredSize(new Dimension(150, 20));
		mapperPackageField.addActionListener(event -> {
			final PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Mapper Package", project);
			packageChooserDialog.selectPackage(mapperPackageField.getText());
			packageChooserDialog.show();

			final PsiPackage psiPackage = packageChooserDialog.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			mapperPackageField.setText(packageName);
		});
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getMapperPackage())) {
			mapperPackageField.setText(entityConfig.getMapperPackage());
		} else {
			mapperPackageField.setText("");
		}
		mapperPackagePanel.add(mapperPackageLabel);
		mapperPackagePanel.add(mapperPackageField);

		examplePackagePanel.setLayout(new BoxLayout(examplePackagePanel, BoxLayout.X_AXIS));

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
		examplePackageField.setText(entityConfig.getExamplePackage());
		examplePackagePanel.add(examplePackageLabel);
		examplePackagePanel.add(examplePackageField);
		examplePackagePanel.setVisible(entityConfig.isUseExample());

		JPanel xmlPackagePanel = new JPanel();
		xmlPackagePanel.setLayout(new BoxLayout(xmlPackagePanel, BoxLayout.X_AXIS));
		JLabel xmlPackageLabel = new JLabel("Xml Package:");
		xmlPackageLabel.setPreferredSize(new Dimension(150, 20));
		xmlPackageField.setText(entityConfig.getXmlPackage());
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

	private void initEntityPanel(String tableName, String modelName, String primaryKey) {
		JPanel entityPanel = new JPanel();
		entityPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		TitledSeparator separator = new TitledSeparator();
		separator.setText("Entity");
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
				entityNameField.setText(entityName);
				mapperNameField.setText(getMapperName(entityName));
				exampleNameField.setText(getExampleName(entityName));
			}
		});

		JPanel entityNamePanel = new JPanel();
		entityNamePanel.setLayout(new BoxLayout(entityNamePanel, BoxLayout.X_AXIS));
		JLabel entityNameLabel = new JLabel("Entity Name:");
		entityNameLabel.setPreferredSize(new Dimension(150, 20));
		entityNamePanel.add(entityNameLabel);
		entityNamePanel.add(entityNameField);
		entityPanel.add(entityNamePanel);
		if (psiElements.length > 1) {
			entityNameField.addFocusListener(new JTextFieldHintListener(entityNameField, "eg:DbTable"));
		} else {
			entityNameField.setText(modelName);
		}
		entityNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				mapperNameField.setText(getMapperName(entityNameField.getText()));
				exampleNameField.setText(getExampleName(entityNameField.getText()));
			}
		});

		//MapperName
		JPanel mapperNamePanel = new JPanel();
		mapperNamePanel.setLayout(new BoxLayout(mapperNamePanel, BoxLayout.X_AXIS));
		JLabel mapperNameLabel = new JLabel("Mapper Name:");
		mapperNameLabel.setPreferredSize(new Dimension(150, 20));
		mapperNameLabel.setLabelFor(entityNameField);
		mapperNamePanel.add(mapperNameLabel);
		mapperNamePanel.add(mapperNameField);
		if (psiElements.length > 1) {
			if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getMapperPostfix())) {
				mapperNameField.addFocusListener(new JTextFieldHintListener(mapperNameField, "eg:DbTable" + entityConfig.getMapperPostfix()));
			} else {
				mapperNameField.addFocusListener(new JTextFieldHintListener(mapperNameField, "eg:DbTable" + "Mapper"));
			}
		} else {
			mapperNameField.setText(getMapperName(modelName));
		}

		exampleNamePanel.setLayout(new BoxLayout(exampleNamePanel, BoxLayout.X_AXIS));
		JLabel exampleNameLabel = new JLabel("Example Name:");
		exampleNameLabel.setPreferredSize(new Dimension(150, 20));
		exampleNameLabel.setLabelFor(entityNameField);
		exampleNamePanel.add(exampleNameLabel);
		exampleNamePanel.add(exampleNameField);
		if (psiElements.length > 1) {
			if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getExamplePostfix())) {
				exampleNameField.addFocusListener(new JTextFieldHintListener(exampleNameField, "eg:DbTable" + entityConfig.getExamplePostfix()));
			} else {
				exampleNameField.addFocusListener(new JTextFieldHintListener(exampleNameField, "eg:DbTable" + "Example"));
			}
		} else {
			exampleNameField.setText(getExampleName(modelName));
		}

		exampleNamePanel.setVisible(entityConfig.isUseExample());

		entityPanel.add(tableNamePanel);
		entityPanel.add(entityNamePanel);
		entityPanel.add(mapperNamePanel);
		entityPanel.add(exampleNamePanel);
	}

	public void generate(RawConnectionConfig connectionConfig) {
		entityConfig.setName(tableNameField.getText());
		entityConfig.setTableName(tableNameField.getText());
		entityConfig.setModuleRootPath(moduleRootField.getText());

		entityConfig.setBasePackage(basePackageField.getText());
		entityConfig.setEntityPackage(entityPackageField.getText());
		entityConfig.setMapperPackage(mapperPackageField.getText());
		entityConfig.setExamplePackage(examplePackageField.getText());
		entityConfig.setXmlPackage(xmlPackageField.getText());

		entityConfig.setMapperName(mapperNameField.getText());
		entityConfig.setEntityName(entityNameField.getText());
		entityConfig.setPrimaryKey(primaryKeyField.getText());
		entityConfig.setExampleName(exampleNameField.getText());

		entityConfig.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
		entityConfig.setComment(commentBox.getSelectedObjects() != null);
		entityConfig.setOverride(overrideBox.getSelectedObjects() != null);
		entityConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
		entityConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
		entityConfig.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
		entityConfig.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
		entityConfig.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
		entityConfig.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
		entityConfig.setAnnotation(annotationBox.getSelectedObjects() != null);
		entityConfig.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
		entityConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
		entityConfig.setUseExample(useExampleBox.getSelectedObjects() != null);
		entityConfig.setMysql8(mysql8Box.getSelectedObjects() != null);
		entityConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
		entityConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);

		entityConfig.setSourcePath(this.entityConfig.getSourcePath());
		entityConfig.setResourcePath(this.entityConfig.getResourcePath());

		new MyBatisGenerateCommand(entityConfig).execute(project, connectionConfig);

	}

	private String getMapperName(String entityName) {
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getMapperPostfix())) {
			return entityName + entityConfig.getMapperPostfix();
		} else {
			return (entityName + "Mapper");
		}
	}

	private String getMapperPostfix() {
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getMapperPostfix())) {
			return entityConfig.getMapperPostfix();
		} else {
			return "Mapper";
		}
	}

	private String getExamplePostfix() {
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getExamplePostfix())) {
			return entityConfig.getExamplePostfix();
		} else {
			return "Example";
		}
	}

	private String getExampleName(String entityName) {
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getExamplePostfix())) {
			return entityName + entityConfig.getExamplePostfix();
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
