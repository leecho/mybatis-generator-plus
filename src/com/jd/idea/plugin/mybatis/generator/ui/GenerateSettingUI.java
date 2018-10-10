package com.jd.idea.plugin.mybatis.generator.ui;

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
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.ui.panel.ProgressPanelBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.jd.idea.plugin.mybatis.generator.contants.PluginContants;
import com.jd.idea.plugin.mybatis.generator.generate.MyBatisCodeGenerator;
import com.jd.idea.plugin.mybatis.generator.model.Credential;
import com.jd.idea.plugin.mybatis.generator.model.EntityConfig;
import com.jd.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.jd.idea.plugin.mybatis.generator.model.TableInfo;
import com.jd.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.jd.idea.plugin.mybatis.generator.util.DatabaseUtils;
import com.jd.idea.plugin.mybatis.generator.util.JTextFieldHintListener;
import com.jd.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.openapi.progress.util.ProgressWrapper;
import com.sun.deploy.ui.ProgressDialog;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
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
	private TextFieldWithBrowseButton entityPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton mapperPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton xmlPackageField = new TextFieldWithBrowseButton();
	private JTextField mapperNameField = new JBTextField(20);
	private JTextField entityNameField = new JBTextField(20);
	private JTextField primaryKeyField = new JBTextField(20);

	private JLabel connectStatusLabel = new JLabel();

	private JCheckBox offsetLimitBox = new JCheckBox("Page(分页)");
	private JCheckBox commentBox = new JCheckBox("comment(实体注释)");
	private JCheckBox overrideXMLBox = new JCheckBox("Overwrite-Xml");
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
			entityConfig.setSourcePath(globalConfig.getSourcePath());
			entityConfig.setResourcePath(globalConfig.getResourcePath());
			entityConfig.setEntityPackage(globalConfig.getEntityPackage());
			entityConfig.setMapperPackage(globalConfig.getMapperPackage());
			entityConfig.setMapperPostfix(globalConfig.getMapperPostfix());
			entityConfig.setXmlPackage(globalConfig.getXmlPackage());

			entityConfig.setOffsetLimit(globalConfig.isOffsetLimit());
			entityConfig.setComment(globalConfig.isComment());
			entityConfig.setOverrideXML(globalConfig.isOverrideXML());
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

	@Override
	protected void doOKAction() {
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

		super.doOKAction();

		this.generate(connectionConfig);

	}

	private boolean testConnection(RawConnectionConfig connectionConfig, Credential credential) {
		String url = connectionConfig.getUrl();
		CredentialAttributes credentialAttributes = new CredentialAttributes(PluginContants.PLUGIN_NAME + "-" + url, credential.getUsername(), this.getClass(), false);
		String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
		ProgressWindow progressWindow = new ProgressWindow(true,project);
		progressWindow.setTitle("Connecting database");
		try {
			progressWindow.start();
			connectStatusLabel.setText("Connecting database");
			DatabaseUtils.testConnection(connectionConfig.getDriverClass(), connectionConfig.getUrl(), credential.getUsername(), password, mysql8Box.getSelectedObjects() != null);
			return true;
		} catch (ClassNotFoundException e) {
			connectStatusLabel.setText("Connect database failed");
			Messages.showMessageDialog(project, "Failed to connect to database, \nmessage: " + e.getMessage(), "Test Connection", Messages.getErrorIcon());
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			connectStatusLabel.setText("Connect database failed");
			Messages.showMessageDialog(project, "Failed to connect to database, \nmessage: " + e.getMessage(), "Test Connection", Messages.getErrorIcon());
			boolean result = getDatabaseCredential(connectionConfig);
			if (result) {
				Map<String, Credential> credentials = myBatisGeneratorConfiguration.getCredentials();
				return testConnection(connectionConfig, credentials.get(connectionConfig.getUrl()));
			} else {
				return false;
			}
		}finally {
			progressWindow.dispose();
		}
	}

	private boolean getDatabaseCredential(RawConnectionConfig connectionConfig) {
		DatabaseCredentialUI databaseCredentialUI = new DatabaseCredentialUI(anActionEvent.getProject(), connectionConfig.getUrl());
		return databaseCredentialUI.showAndGet();
	}

	private void initOptionsPanel() {
		JBPanel optionsPanel = new JBPanel(new GridLayout(8, 2, 10, 10));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		optionsPanel.add(offsetLimitBox);
		optionsPanel.add(commentBox);
		optionsPanel.add(overrideXMLBox);
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

		offsetLimitBox.setSelected(entityConfig.isOffsetLimit());
		commentBox.setSelected(entityConfig.isComment());
		overrideXMLBox.setSelected(entityConfig.isOverrideXML());
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

	private void initPackagePanel() {
		JPanel entityPackagePanel = new JPanel();
		entityPackagePanel.setLayout(new BoxLayout(entityPackagePanel, BoxLayout.X_AXIS));
		JBLabel entityPackageLabel = new JBLabel("Entity Class Package:");
		entityPackageLabel.setPreferredSize(new Dimension(200, 20));
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
		JLabel mapperPackageLabel = new JLabel("Mapper Class Package:");
		mapperPackageLabel.setPreferredSize(new Dimension(200, 20));
		mapperPackageField.addActionListener(e -> {
			final PackageChooserDialog chooser = new PackageChooserDialog("Select Mapper Package", project);
			chooser.selectPackage(mapperPackageField.getText());
			chooser.show();
			final PsiPackage psiPackage = chooser.getSelectedPackage();
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

		JPanel xmlPackagePanel = new JPanel();
		xmlPackagePanel.setLayout(new BoxLayout(xmlPackagePanel, BoxLayout.X_AXIS));
		JLabel xmlPackageLabel = new JLabel("Mapper Xml Package:");
		xmlPackageLabel.setPreferredSize(new Dimension(200, 20));
		xmlPackageField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				xmlPackageField.setText(xmlPackageField.getText().replaceAll("\\\\", "/"));
			}
		});
		xmlPackageField.setText(entityConfig.getXmlPackage());
		xmlPackagePanel.add(xmlPackageLabel);
		xmlPackagePanel.add(xmlPackageField);

		JPanel packagePanel = new JPanel();
		packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		packagePanel.setBorder(BorderFactory.createTitledBorder("Package"));
		packagePanel.add(entityPackagePanel);
		packagePanel.add(mapperPackagePanel);
		packagePanel.add(xmlPackagePanel);
		contentPane.add(packagePanel);
	}

	private void initEntityPanel(String tableName, String modelName, String primaryKey) {
		JPanel entityPanel = new JPanel();
		entityPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		entityPanel.setBorder(BorderFactory.createTitledBorder("Entity"));
		contentPane.add(entityPanel);

		//Table
		JPanel tableNamePanel = new JPanel();
		tableNamePanel.setLayout(new BoxLayout(tableNamePanel, BoxLayout.X_AXIS));
		JLabel tableLabel = new JLabel("Table Name:");
		tableLabel.setLabelFor(tableNameField);
		tableLabel.setPreferredSize(new Dimension(200, 20));
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
			}
		});

		//PrimaryKey
		/*JPanel primaryKeyPanel = new JPanel();
		primaryKeyPanel.setLayout(new BoxLayout(primaryKeyPanel, BoxLayout.X_AXIS));
		JLabel primaryKeyLabel = new JLabel("Primary Key:");
		primaryKeyLabel.setPreferredSize(new Dimension(200, 20));
		primaryKeyLabel.setLabelFor(primaryKeyField);
		primaryKeyPanel.add(primaryKeyLabel);
		primaryKeyPanel.add(primaryKeyField);
		if (psiElements.length > 1) {
			primaryKeyField.addFocusListener(new JTextFieldHintListener(primaryKeyField, "eg:primary key"));
		} else {
			primaryKeyField.setText(primaryKey);
		}*/

		//EntityName
		JPanel entityNamePanel = new JPanel();
		entityNamePanel.setLayout(new BoxLayout(entityNamePanel, BoxLayout.X_AXIS));
		JLabel entityNameLabel = new JLabel("Entity Name:");
		entityNameLabel.setPreferredSize(new Dimension(200, 20));
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
			}
		});

		//MapperName
		JPanel mapperNamePanel = new JPanel();
		mapperNamePanel.setLayout(new BoxLayout(mapperNamePanel, BoxLayout.X_AXIS));
		JLabel mapperNameLabel = new JLabel("Mapper Name:");
		mapperNameLabel.setPreferredSize(new Dimension(200, 20));
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

		entityPanel.add(tableNamePanel);
		//entityPanel.add(primaryKeyPanel);
		entityPanel.add(entityNamePanel);
		entityPanel.add(mapperNamePanel);
	}

	public void generate(RawConnectionConfig connectionConfig) {
		entityConfig.setName(tableNameField.getText());
		entityConfig.setTableName(tableNameField.getText());
		entityConfig.setProjectRootPath(project.getBasePath());

		entityConfig.setEntityPackage(entityPackageField.getText());
		entityConfig.setMapperPackage(mapperPackageField.getText());
		entityConfig.setXmlPackage(xmlPackageField.getText());
		entityConfig.setMapperName(mapperNameField.getText());
		entityConfig.setEntityName(entityNameField.getText());
		entityConfig.setPrimaryKey(primaryKeyField.getText());

		entityConfig.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
		entityConfig.setComment(commentBox.getSelectedObjects() != null);
		entityConfig.setOverrideXML(overrideXMLBox.getSelectedObjects() != null);
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

		new MyBatisCodeGenerator(entityConfig).execute(project, connectionConfig);

	}

	private String getMapperName(String entityName) {
		if (entityConfig != null && !StringUtils.isEmpty(entityConfig.getMapperPostfix())) {
			return entityName + entityConfig.getMapperPostfix();
		} else {
			return (entityName + "Mapper");
		}
	}

	public static void main(String[] args) {
		//new GenerateSettingUI();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return contentPane;
	}
}
