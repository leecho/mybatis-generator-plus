package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.EntityConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableInfo;
import com.github.leecho.idea.plugin.mybatis.generator.model.ColumnSetting;
import com.github.leecho.idea.plugin.mybatis.generator.model.ColumnSettingModel;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jdesktop.swingx.JXTable;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.types.JdbcTypeNameTranslator;

import javax.swing.*;
import java.util.ArrayList;

/**
 * 类型映射设置
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class ColumnSettingUI extends DialogWrapper {
	/**
	 * 主面板
	 */
	private JPanel mainPanel;
	/**
	 * 类型映射表
	 */
	private JTable typeMapperTable;

	/**
	 * 类型映射表模型
	 */
	private ColumnSettingModel columnSettingModel;

	private EntityConfig entityConfig;

	public ColumnSettingUI(Project project, EntityConfig entityConfig, TableInfo tableInfo) {
		super(project);
		//添加类型
		// 初始化操作
		this.entityConfig = entityConfig;
		load(entityConfig, tableInfo);
		typeMapperTable.getColumnModel().setColumnMargin(3);
		typeMapperTable.getColumnModel().getColumn(4).setCellEditor(new JXTable.BooleanEditor());
		this.setTitle("Column Setting");
		this.init();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return mainPanel;
	}

	@Override
	protected void doOKAction() {
		java.util.List<ColumnSetting> columnSettingList = this.columnSettingModel.getData();
		columnSettingList.forEach(columnSetting -> {
			if (columnSetting.getChanged()) {
				columnSetting.setChanged(false);
				entityConfig.getColumnSettings().put(columnSetting.getColumn(), columnSetting);
			}
		});
		super.doOKAction();
	}

	/**
	 * 初始化方法
	 */
	protected void load(EntityConfig entityConfig, TableInfo tableInfo) {
		//初始化表格
		this.columnSettingModel = new ColumnSettingModel();
		JavaTypeResolverDefaultImpl resolver = new JavaTypeResolverDefaultImpl();
		java.util.List<ColumnSetting> columnSettingList = new ArrayList<>();
		tableInfo.getColumns().forEach(dasColumn -> {
			ColumnSetting columnSetting = entityConfig.getColumnSettings().get(dasColumn.getName());
			if (columnSetting == null) {
				columnSetting = new ColumnSetting();
				columnSetting.setColumn(dasColumn.getName());
				String property = StringUtils.dbStringToCamelStyle(dasColumn.getName());
				property = property.substring(0, 1).toLowerCase() + property.substring(1);
				columnSetting.setJavaProperty(property);
				IntrospectedColumn introspectedColumn = new IntrospectedColumn();
				introspectedColumn.setJdbcType(JdbcTypeNameTranslator.getJdbcType(dasColumn.getDataType().typeName.toUpperCase()));
				introspectedColumn.setLength(dasColumn.getDataType().getLength());
				introspectedColumn.setScale(dasColumn.getDataType().getScale());
				columnSetting.setJdbcType(resolver.calculateJdbcTypeName(introspectedColumn));
				columnSetting.setJavaType(resolver.calculateJavaType(introspectedColumn).getShortName());
				columnSetting.setIgnore(false);
			}
			columnSetting.setChanged(false);
			columnSettingList.add(columnSetting);
		});
		columnSettingModel.init(columnSettingList);
		this.typeMapperTable.setModel(columnSettingModel);
	}

}