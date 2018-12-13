package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.openapi.project.Project;
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

	private TextFieldWithBrowseButton entityPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton mapperPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton xmlPackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton examplePackageField = new TextFieldWithBrowseButton();
	private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();

	private JTextField sourcePathField = new JTextField();
	private JTextField resourcePathField = new JTextField();

	private JTextField mapperPostfixField = new JTextField(10);
	private JTextField examplePostfixField = new JTextField(10);

	private JCheckBox offsetLimitBox = new JCheckBox("Page(分页)");
	private JCheckBox commentBox = new JCheckBox("Comment(实体注释)");
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
	private JCheckBox mysql_8Box = new JCheckBox("mysql_8");
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

		this.initPathPanel();
		this.initPostfixPanel();
		this.initPackagePanel();
		this.initOptionsPanel();

		GlobalConfig globalConfig = config.getGlobalConfig();
		mapperPostfixField.setText(globalConfig.getMapperPostfix());
		examplePostfixField.setText(globalConfig.getExamplePostfix());
		entityPackageField.setText(globalConfig.getEntityPackage());
		mapperPackageField.setText(globalConfig.getMapperPackage());
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
		mysql_8Box.setSelected(globalConfig.isMysql8());
		lombokAnnotationBox.setSelected(globalConfig.isLombokAnnotation());
		lombokBuilderAnnotationBox.setSelected(globalConfig.isLombokBuilderAnnotation());

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
		optionsPanel.add(mysql_8Box);
		optionsPanel.add(lombokAnnotationBox);
		optionsPanel.add(lombokBuilderAnnotationBox);

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
		mapperPackagePanel.add(mapperPackageLabel);
		mapperPackagePanel.add(mapperPackageField);

		JPanel examplePackagePanel = new JPanel();
		examplePackagePanel.setLayout(new BoxLayout(examplePackagePanel, BoxLayout.X_AXIS));
		JLabel examplePackageLabel = new JLabel("Example Class Package:");
		examplePackageLabel.setPreferredSize(new Dimension(200, 20));
		examplePackageField.addActionListener(e -> {
			final PackageChooserDialog chooser = new PackageChooserDialog("Select Example Package", project);
			chooser.selectPackage(examplePackageField.getText());
			chooser.show();
			final PsiPackage psiPackage = chooser.getSelectedPackage();
			String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
			examplePackageField.setText(packageName);
		});
		examplePackagePanel.add(examplePackageLabel);
		examplePackagePanel.add(examplePackageField);

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
		xmlPackageField.setText(globalConfig.getXmlPackage());
		xmlPackagePanel.add(xmlPackageLabel);
		xmlPackagePanel.add(xmlPackageField);

		JPanel packagePanel = new JPanel();
		packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		//packagePanel.setBorder(BorderFactory.createTitledBorder("Package"));
		packagePanel.add(projectRootPanel);
		packagePanel.add(entityPackagePanel);
		packagePanel.add(examplePackagePanel);
		packagePanel.add(xmlPackagePanel);

		TitledSeparator separator = new TitledSeparator();
		separator.setText("Package");
		contentPanel.add(separator);
		contentPanel.add(packagePanel);
	}

	public boolean isModified() {
		boolean modified = true;
//        modified |= !this.id.getText().equals(config.getId());
//        modified |= !this.entity.getText().equals(config.getEntity());
//        modified |= !this.project_directory.getText().equals(config.getProject_directory());
//        modified |= !this.dao_name.getText().equals(config.getDao_name());
//
//        modified |= !this.entity_package.getText().equals(config.getEntity_package());
//        modified |= !this.entity_directory.getText().equals(config.getEntity_directory());
//        modified |= !this.mapper_package.getText().equals(config.getMapper_package());
//        modified |= !this.mapper_directory.getText().equals(config.getMapper_directory());
//        modified |= !this.xml_package.getText().equals(config.getXml_package());
//        modified |= !this.xml_directory.getText().equals(config.getXml_directory());
//        modified |= !this.password.getPassword().equals(config.getPassword());
//        modified |= !this.username.getText().equals(config.getUsername());
		return modified;
	}

	public void apply() {
		GlobalConfig globalConfig = new GlobalConfig();
		globalConfig.setMapperPostfix(mapperPostfixField.getText());
		globalConfig.setExamplePostfix(examplePostfixField.getText());
		globalConfig.setEntityPackage(entityPackageField.getText());
		globalConfig.setMapperPackage(mapperPackageField.getText());
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
		globalConfig.setMysql8(mysql_8Box.getSelectedObjects() != null);
		globalConfig.setLombokAnnotation(lombokAnnotationBox.getSelectedObjects() != null);
		globalConfig.setLombokBuilderAnnotation(lombokBuilderAnnotationBox.getSelectedObjects() != null);

		globalConfig.setSourcePath(sourcePathField.getText());
		globalConfig.setResourcePath(resourcePathField.getText());

		this.config.setGlobalConfig(globalConfig);


	}

	public void reset() {

	}

	@Override
	public JPanel getContentPane() {
		return contentPanel;
	}


}
