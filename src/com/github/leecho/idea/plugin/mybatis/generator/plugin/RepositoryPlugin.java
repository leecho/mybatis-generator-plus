package com.github.leecho.idea.plugin.mybatis.generator.plugin;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class RepositoryPlugin extends PluginAdapter {
  private FullyQualifiedJavaType annotationRepository = new FullyQualifiedJavaType("org.springframework.stereotype.Repository");
  private String annotation = "@Repository";

  public RepositoryPlugin() {
  }

  public boolean validate(List<String> list) {
    return true;
  }

  public boolean clientGenerated(
      Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    interfaze.addImportedType(this.annotationRepository);
    interfaze.addAnnotation(this.annotation);
    return true;
  }
}
