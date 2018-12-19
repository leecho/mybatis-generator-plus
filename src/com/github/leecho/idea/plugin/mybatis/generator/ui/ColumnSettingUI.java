package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.TableConfig;
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
import java.sql.Types;
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
	private JTable columnSettingTable;

	/**
	 * 类型映射表模型
	 */
	private ColumnSettingModel columnSettingModel;

	private TableConfig tableConfig;

	public ColumnSettingUI(Project project, TableConfig tableConfig, TableInfo tableInfo) {
		super(project);
		//添加类型
		// 初始化操作
		this.tableConfig = tableConfig;
		load(tableConfig, tableInfo);
		columnSettingTable.getColumnModel().setColumnMargin(3);
		columnSettingTable.getColumnModel().getColumn(4).setCellEditor(new JXTable.BooleanEditor());
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
				tableConfig.getColumnSettings().put(columnSetting.getColumn(), columnSetting);
			}
		});
		super.doOKAction();
	}

	/**
	 * 初始化方法
	 */
	protected void load(TableConfig tableConfig, TableInfo tableInfo) {
		//初始化表格
		this.columnSettingModel = new ColumnSettingModel();
		JavaTypeResolverDefaultImpl resolver = new JavaTypeResolverDefaultImpl();
		java.util.List<ColumnSetting> columnSettingList = new ArrayList<>();
		tableInfo.getColumns().forEach(dasColumn -> {
			ColumnSetting columnSetting = tableConfig.getColumnSettings().get(dasColumn.getName());
			if (columnSetting == null) {
				columnSetting = new ColumnSetting();
				columnSetting.setColumn(dasColumn.getName());
				String property = StringUtils.dbStringToCamelStyle(dasColumn.getName());
				property = property.substring(0, 1).toLowerCase() + property.substring(1);
				columnSetting.setJavaProperty(property);
				IntrospectedColumn introspectedColumn = new IntrospectedColumn();
				String typeName = dasColumn.getDataType().typeName.toUpperCase();
				if("DATETIME".equals(typeName)){
					introspectedColumn.setJdbcType(Types.TIMESTAMP);
				}else if("INT".equals(typeName)){
					introspectedColumn.setJdbcType(Types.INTEGER);
				}else{
					introspectedColumn.setJdbcType(JdbcTypeNameTranslator.getJdbcType(typeName));
				}
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
		this.columnSettingTable.setModel(columnSettingModel);
	}

}