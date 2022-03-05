
/**
 * @author LIQIU
 * created on 2019/6/20
 **/
package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.ColumnSetting;
import com.github.leecho.idea.plugin.mybatis.generator.model.ColumnSettingModel;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableInfo;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import org.jdesktop.swingx.JXTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import javax.swing.*;
import java.awt.*;
import java.sql.Types;
import java.util.ArrayList;

/**
 * @author LIQIU
 * created on 2019/6/20
 **/
@Deprecated
public class ColumnTablePanel extends JBPanel {

    /**
     * 类型映射表
     */
    private JTable columnSettingTable = new JBTable();

    /**
     * 类型映射表模型
     */
    private ColumnSettingModel columnSettingModel;

    private TableConfig tableConfig;

    public ColumnTablePanel(TableConfig tableConfig, TableInfo tableInfo) {
        //添加类型
        // 初始化操作
        this.tableConfig = tableConfig;
        load(tableConfig, tableInfo);
        this.setName("Columns");
        columnSettingTable.getColumnModel().setColumnMargin(3);
        columnSettingTable.getColumnModel().getColumn(4).setCellEditor(new JXTable.BooleanEditor());
        columnSettingTable.getColumnModel().getColumn(3).setWidth(50);
        VerticalFlowLayout layout= new VerticalFlowLayout(VerticalFlowLayout.TOP);
        layout.setVgap(0);
        layout.setHgap(0);
        layout.setHorizontalFill(true);
        layout.setVerticalFill(true);
        this.setLayout(layout);
        JBScrollPane jScrollPane = new JBScrollPane(columnSettingTable);
        jScrollPane.setPreferredSize(new Dimension(800,320));
        this.setBorder(JBUI.Borders.empty(-11));
        this.add(jScrollPane);
        this.setVisible(true);
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
            IntrospectedColumn introspectedColumn = new IntrospectedColumn();
            String typeName = dasColumn.getDataType().typeName.toUpperCase();
            if ("DATETIME".equals(typeName)) {
                introspectedColumn.setJdbcType(Types.TIMESTAMP);
            } else if ("INT".equals(typeName)) {
                introspectedColumn.setJdbcType(Types.INTEGER);
            } else {
                introspectedColumn.setJdbcTypeName(typeName);
            }
            introspectedColumn.setLength(dasColumn.getDataType().getLength());
            introspectedColumn.setScale(dasColumn.getDataType().getScale());

            if (columnSetting == null) {
                columnSetting = new ColumnSetting();
                columnSetting.setColumn(dasColumn.getName());
                String property = StringUtils.dbStringToCamelStyle(dasColumn.getName());
                property = property.substring(0, 1).toLowerCase() + property.substring(1);
                columnSetting.setJavaProperty(property);
                columnSetting.setJdbcType(resolver.calculateJdbcTypeName(introspectedColumn));
                columnSetting.setJavaType(resolver.calculateJavaType(introspectedColumn).getShortName());
                columnSetting.setIgnore(false);
            } else {
                if (!columnSetting.getJdbcType().equals(resolver.calculateJdbcTypeName(introspectedColumn))) {
                    columnSetting.setJdbcType(resolver.calculateJdbcTypeName(introspectedColumn));
                    columnSetting.setJavaType(resolver.calculateJavaType(introspectedColumn).getShortName());
                }
            }
            columnSetting.setComment(dasColumn.getComment());
            columnSetting.setChanged(false);
            columnSettingList.add(columnSetting);
        });
        columnSettingModel.init(columnSettingList);
        columnSettingModel.addTableModelListener(e -> {
            ColumnSetting columnSetting = columnSettingModel.getData().get(e.getFirstRow());
            columnSetting.setChanged(false);
            tableConfig.getColumnSettings().put(columnSetting.getColumn(),columnSetting);
        });
        this.columnSettingTable.setModel(columnSettingModel);
    }
}
