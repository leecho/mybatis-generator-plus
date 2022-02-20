package com.github.leecho.idea.plugin.mybatis.generator.model;

/**
 * 数据库链接相关配置
 */
public class ConnectionConfig {

  private String name;
  private String driverClass;
  private String url;
  private String dataBaseName;
  private String dataBaseVersion;
  private String schema;

  public ConnectionConfig(String name, String driverClass, String url) {
    this.name = name;
    this.driverClass = driverClass;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDriverClass() {
    return driverClass;
  }

  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDataBaseName() {
    return dataBaseName;
  }

  public void setDataBaseName(String dataBaseName) {
    this.dataBaseName = dataBaseName;
  }

  public String getDataBaseVersion() {
    return dataBaseVersion;
  }

  public void setDataBaseVersion(String dataBaseVersion) {
    this.dataBaseVersion = dataBaseVersion;
  }

  public boolean isMysql8() {
    return this.driverClass.contains("mysql") && dataBaseVersion.startsWith("8.");
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    // todo 其他类型数据库待处理
    if (this.url.contains("mysql") && !this.url.contains(schema)) {
      this.url = this.url + "/" + schema;
    }
    this.schema = schema;
  }

}
