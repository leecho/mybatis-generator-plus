package com.github.leecho.idea.plugin.mybatis.generator.model;

/**
 * 各种类型的java包的初始路径
 */
public class PackageInitialPath {

  private String basePackageInitialPath;
  private String domainPackageInitialPath;
  private String mapperPackageInitialPath;
  private String examplePackageInitialPath;
  private String xmlPackageInitialPath;

  public String getBasePackageInitialPath() {
    return basePackageInitialPath;
  }

  public void setBasePackageInitialPath(String basePackageInitialPath) {
    this.basePackageInitialPath = basePackageInitialPath;
  }

  public String getDomainPackageInitialPath() {
    return domainPackageInitialPath;
  }

  public void setDomainPackageInitialPath(String domainPackageInitialPath) {
    this.domainPackageInitialPath = domainPackageInitialPath;
  }

  public String getMapperPackageInitialPath() {
    return mapperPackageInitialPath;
  }

  public void setMapperPackageInitialPath(String mapperPackageInitialPath) {
    this.mapperPackageInitialPath = mapperPackageInitialPath;
  }

  public String getExamplePackageInitialPath() {
    return examplePackageInitialPath;
  }

  public void setExamplePackageInitialPath(String examplePackageInitialPath) {
    this.examplePackageInitialPath = examplePackageInitialPath;
  }

  public String getXmlPackageInitialPath() {
    return xmlPackageInitialPath;
  }

  public void setXmlPackageInitialPath(String xmlPackageInitialPath) {
    this.xmlPackageInitialPath = xmlPackageInitialPath;
  }

}
