package com.github.leecho.idea.plugin.mybatis.generator.plugin;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

public class DbRemarksCommentGenerator implements CommentGenerator {
  private Properties properties = new Properties();
  private boolean columnRemarks;
  private boolean isAnnotations;

  public DbRemarksCommentGenerator() {
  }

  public void addJavaFileComment(CompilationUnit compilationUnit) {
    if (this.isAnnotations) {
      compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Table"));
      compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Id"));
      compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Column"));
      compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.GeneratedValue"));
      compilationUnit.addImportedType(new FullyQualifiedJavaType("org.hibernate.validator.constraints.NotEmpty"));
    }

  }

  public void addComment(XmlElement xmlElement) {
  }

  public void addRootComment(XmlElement rootElement) {
  }

  public void addGeneralMethodAnnotation(
      Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
  }

  public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
  }

  public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
  }

  public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
  }

  public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
  }

  public void addConfigurationProperties(Properties properties) {
    this.properties.putAll(properties);
    this.columnRemarks = StringUtility.isTrue(properties.getProperty("columnRemarks"));
    this.isAnnotations = StringUtility.isTrue(properties.getProperty("annotations"));
  }

  public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
  }

  public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    topLevelClass.addJavaDocLine("/**");
    topLevelClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
    topLevelClass.addJavaDocLine(" * @author ");
    topLevelClass.addJavaDocLine(" */");
    if (this.isAnnotations) {
      topLevelClass.addAnnotation("@Table(name=\"" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\")");
    }

  }

  public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
  }

  public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
    if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
      field.addJavaDocLine("/**");
      StringBuilder sb = new StringBuilder();
      sb.append(" * ");
      sb.append(introspectedColumn.getRemarks());
      field.addJavaDocLine(sb.toString());
      field.addJavaDocLine(" */");
    }

    if (this.isAnnotations) {
      boolean isId = false;
      Iterator var5 = introspectedTable.getPrimaryKeyColumns().iterator();

      while(var5.hasNext()) {
        IntrospectedColumn column = (IntrospectedColumn)var5.next();
        if (introspectedColumn == column) {
          isId = true;
          field.addAnnotation("@Id");
          field.addAnnotation("@GeneratedValue");
          break;
        }
      }

      if (!introspectedColumn.isNullable() && !isId) {
        field.addAnnotation("@NotEmpty");
      }

      if (introspectedColumn.isIdentity()) {
        if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
          field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
        } else {
          field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
        }
      } else if (introspectedColumn.isSequenceColumn()) {
        field.addAnnotation("@SequenceGenerator(name=\"\",sequenceName=\"" + introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement() + "\")");
      }
    }

  }

  public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
  }

  public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
  }

  public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
  }

  public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
  }

  public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
    innerClass.addJavaDocLine("/**");
    innerClass.addJavaDocLine(" */");
  }
}
