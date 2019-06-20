package com.github.leecho.idea.plugin.mybatis.generator.plugin;

import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;


/**
 * A MyBatis Generator plugin to use Lombok's annotations.
 * For example, use @Data annotation instead of getter ands setter.
 *
 * @author Paolo Predonzani (http://softwareloop.com/)
 */
public class SwaggerPlugin extends PluginAdapter {

    /**
     * @param warnings list of warnings
     * @return always true
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * Intercepts base record class generation
     *
     * @param topLevelClass     the generated base record class
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return always true
     */
    @Override
    public boolean modelBaseRecordClassGenerated(
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        addAnnotations(topLevelClass, introspectedTable.getRemarks());
        return true;
    }

    /**
     * Intercepts primary key class generation
     *
     * @param topLevelClass     the generated primary key class
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return always true
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        addAnnotations(topLevelClass, introspectedTable.getRemarks());
        return true;
    }

    /**
     * Intercepts "record with blob" class generation
     *
     * @param topLevelClass     the generated record with BLOBs class
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return always true
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        addAnnotations(topLevelClass, introspectedTable.getRemarks());
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (!StringUtils.isEmpty(introspectedColumn.getRemarks())) {
            addFieldAnnotations(topLevelClass, field, introspectedColumn.getRemarks());
        }
        return true;
    }

    /**
     * Adds the lombok annotations' imports and annotations to the class
     *
     * @param topLevelClass the partially implemented model class
     */
    private void addFieldAnnotations(TopLevelClass topLevelClass, Field field, String name) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty"));
        field.addAnnotation(String.format("@ApiModelProperty(\"%s\")", name));
    }

    /**
     * Adds the lombok annotations' imports and annotations to the class
     *
     * @param topLevelClass the partially implemented model class
     */
    private void addAnnotations(TopLevelClass topLevelClass, String name) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModel"));
        String[] parts = name.split("\\.");
        topLevelClass.addAnnotation(String.format("@ApiModel(\"%s\")", parts[parts.length - 1]));
    }


    @Override
    public boolean clientGenerated(
            Interface interfaze,
            TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable
    ) {
        interfaze.addImportedType(new FullyQualifiedJavaType(
                "org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");
        return true;
    }
}
