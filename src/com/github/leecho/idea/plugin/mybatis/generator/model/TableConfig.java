package com.github.leecho.idea.plugin.mybatis.generator.model;

import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgTargetRuntimeEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * 界面配置
 */
public class TableConfig {

  /**
   * 配置名称
   */
  private String name;

  /**
   * 表名
   */
  private String tableName;

  /**
   * 主键
   */
  private String primaryKey;

  /**
   * 实体名
   */
  private String domainName;

  /**
   * mapper名称
   */
  private String mapperName;

  /**
   * example名称
   */
  private String exampleName;

  /**
   * domain后缀
   */
  private String domainPostfix;

  /**
   * dao后缀
   */
  private String mapperPostfix;

  /**
   * dao后缀
   */
  private String examplePostfix;

  /**
   * 工程目录
   */
  private String moduleRootPath;

  private String sourcePath;
  private String resourcePath;

  private String basePackage;
  private String domainPackage;
  private String mapperPackage;
  private String examplePackage;
  private String xmlPackage;
  /**
   * mybatis generator runtime
   * @see MbgTargetRuntimeEnum
   */
  private String mgbTargetRuntime;
  /**
   * mybatis generator java client configuration type
   * @see
   */
  private String mgbJavaClientConfigType;

  private Map<String, ColumnSetting> columnSettings = new HashMap<>();

  /**
   * 是否生成实体注释（来自表）
   */
  private boolean comment;

  /**
   * 是否覆盖原xml
   */
  private boolean override;

  /**
   * 是否生成toString/hashCode/equals方法
   */
  private boolean needToStringHashcodeEquals;

  /**
   * 是否使用Schema前缀
   */
  private boolean useSchemaPrefix;

  /**
   * 是否DAO使用 @Repository 注解
   */
  private boolean annotationDAO;

  /**
   * 是否DAO方法抽出到公共父接口
   */
  private boolean useDAOExtendStyle;

  /**
   * 是否JSR310: Date and Time API
   */
  private boolean jsr310Support;

  /**
   * 是否生成JPA注解
   */
  private boolean annotation;

  /**
   * 是否使用实际的列名
   */
  private boolean useActualColumnNames;
  /**
   * 是否启用as别名查询
   */
  private boolean useTableNameAlias;
  /**
   * 是否使用Example
   */
  private boolean useExample;
  /**
   * 是否是mysql8数据库
   */
  private boolean mysql8;

  private boolean lombokAnnotation;

  private boolean lombokBuilderAnnotation;


  private String encoding;
  private String connectorJarPath;

  public boolean isJsr310Support() {
    return jsr310Support;
  }

  public void setJsr310Support(boolean jsr310Support) {
    this.jsr310Support = jsr310Support;
  }

  public boolean isUseSchemaPrefix() {
    return useSchemaPrefix;
  }

  public void setUseSchemaPrefix(boolean useSchemaPrefix) {
    this.useSchemaPrefix = useSchemaPrefix;
  }

  public boolean isUseExample() {
    return useExample;
  }

  public void setUseExample(boolean useExample) {
    this.useExample = useExample;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getConnectorJarPath() {
    return connectorJarPath;
  }

  public void setConnectorJarPath(String connectorJarPath) {
    this.connectorJarPath = connectorJarPath;
  }

  public String getModuleRootPath() {
    return moduleRootPath;
  }

  public void setModuleRootPath(String moduleRootPath) {
    this.moduleRootPath = moduleRootPath;
  }

  public String getDomainPackage() {
    return domainPackage;
  }

  public void setDomainPackage(String domainPackage) {
    this.domainPackage = domainPackage;
  }


  public String getMapperPackage() {
    return mapperPackage;
  }

  public void setMapperPackage(String mapperPackage) {
    this.mapperPackage = mapperPackage;
  }


  public String getXmlPackage() {
    return xmlPackage;
  }

  public void setXmlPackage(String xmlPackage) {
    this.xmlPackage = xmlPackage;
  }

  public boolean isComment() {
    return comment;
  }

  public void setComment(boolean comment) {
    this.comment = comment;
  }

  public boolean isNeedToStringHashcodeEquals() {
    return needToStringHashcodeEquals;
  }

  public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
    this.needToStringHashcodeEquals = needToStringHashcodeEquals;
  }


  public boolean isAnnotationDAO() {
    return annotationDAO;
  }

  public void setAnnotationDAO(boolean annotationDAO) {
    this.annotationDAO = annotationDAO;
  }

  public boolean isAnnotation() {
    return annotation;
  }

  public void setAnnotation(boolean annotation) {
    this.annotation = annotation;
  }

  public boolean isUseActualColumnNames() {
    return useActualColumnNames;
  }

  public void setUseActualColumnNames(boolean useActualColumnNames) {
    this.useActualColumnNames = useActualColumnNames;
  }

  public String getMapperName() {
    return mapperName;
  }

  public void setMapperName(String mapperName) {
    this.mapperName = mapperName;
  }

  public String getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public boolean isOverride() {
    return override;
  }

  public void setOverride(boolean override) {
    this.override = override;
  }

  public void setUseDAOExtendStyle(boolean useDAOExtendStyle) {
    this.useDAOExtendStyle = useDAOExtendStyle;
  }

  public boolean isUseDAOExtendStyle() {
    return useDAOExtendStyle;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }

  public String getResourcePath() {
    return resourcePath;
  }

  public void setResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  public String getMapperPostfix() {
    return mapperPostfix;
  }

  public void setMapperPostfix(String mapperPostfix) {
    this.mapperPostfix = mapperPostfix;
  }

  public boolean isMysql8() {
    return mysql8;
  }

  public void setMysql8(boolean mysql8) {
    this.mysql8 = mysql8;
  }

  public boolean isLombokAnnotation() {
    return lombokAnnotation;
  }

  public void setLombokAnnotation(boolean lombokAnnotation) {
    this.lombokAnnotation = lombokAnnotation;
  }

  public boolean isLombokBuilderAnnotation() {
    return lombokBuilderAnnotation;
  }

  public void setLombokBuilderAnnotation(boolean lombokBuilderAnnotation) {
    this.lombokBuilderAnnotation = lombokBuilderAnnotation;
  }

  public String getExamplePackage() {
    return examplePackage;
  }

  public void setExamplePackage(String examplePackage) {
    this.examplePackage = examplePackage;
  }

  public String getExamplePostfix() {
    return examplePostfix;
  }

  public void setExamplePostfix(String examplePostfix) {
    this.examplePostfix = examplePostfix;
  }

  public String getExampleName() {
    return exampleName;
  }

  public void setExampleName(String exampleName) {
    this.exampleName = exampleName;
  }

  public String getBasePackage() {
    return basePackage;
  }

  public void setBasePackage(String basePackage) {
    this.basePackage = basePackage;
  }

  public Map<String, ColumnSetting> getColumnSettings() {
    return columnSettings;
  }

  public void setColumnSettings(Map<String, ColumnSetting> columnSettings) {
    this.columnSettings = columnSettings;
  }

  public String getDomainPostfix() {
    return domainPostfix;
  }

  public void setDomainPostfix(String domainPostfix) {
    this.domainPostfix = domainPostfix;
  }

  public String getMgbTargetRuntime() {
    return mgbTargetRuntime;
  }

  public void setMgbTargetRuntime(String mgbTargetRuntime) {
    this.mgbTargetRuntime = mgbTargetRuntime;
  }

  public boolean isUseTableNameAlias() {
    return useTableNameAlias;
  }

  public void setUseTableNameAlias(boolean useTableNameAlias) {
    this.useTableNameAlias = useTableNameAlias;
  }

  public String getMgbJavaClientConfigType() {
    return mgbJavaClientConfigType;
  }

  public void setMgbJavaClientConfigType(String mgbJavaClientConfigType) {
    this.mgbJavaClientConfigType = mgbJavaClientConfigType;
  }
}
