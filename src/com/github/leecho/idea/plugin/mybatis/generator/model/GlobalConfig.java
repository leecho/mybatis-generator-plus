package com.github.leecho.idea.plugin.mybatis.generator.model;

import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgJavaClientConfigTypeEnum;
import com.github.leecho.idea.plugin.mybatis.generator.enums.MbgTargetRuntimeEnum;

public class GlobalConfig {

  private String sourcePath;
  private String resourcePath;
  private String defaultXmlPackage;

  private String domainPostfix;
  private String mapperPostfix;
  private String examplePostfix;

  private String tablePrefix;

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

  private boolean lombokAnnotation;

  private boolean lombokBuilderAnnotation;


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

  public boolean isComment() {
    return comment;
  }

  public void setComment(boolean comment) {
    this.comment = comment;
  }

  public boolean isOverride() {
    return override;
  }

  public void setOverride(boolean override) {
    this.override = override;
  }

  public boolean isNeedToStringHashcodeEquals() {
    return needToStringHashcodeEquals;
  }

  public void setNeedToStringHashcodeEquals(boolean needToStringHashcodeEquals) {
    this.needToStringHashcodeEquals = needToStringHashcodeEquals;
  }

  public boolean isUseSchemaPrefix() {
    return useSchemaPrefix;
  }

  public void setUseSchemaPrefix(boolean useSchemaPrefix) {
    this.useSchemaPrefix = useSchemaPrefix;
  }


  public boolean isAnnotationDAO() {
    return annotationDAO;
  }

  public void setAnnotationDAO(boolean annotationDAO) {
    this.annotationDAO = annotationDAO;
  }

  public boolean isUseDAOExtendStyle() {
    return useDAOExtendStyle;
  }

  public void setUseDAOExtendStyle(boolean useDAOExtendStyle) {
    this.useDAOExtendStyle = useDAOExtendStyle;
  }

  public boolean isJsr310Support() {
    return jsr310Support;
  }

  public void setJsr310Support(boolean jsr310Support) {
    this.jsr310Support = jsr310Support;
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

  public boolean isUseExample() {
    return useExample;
  }

  public void setUseExample(boolean useExample) {
    this.useExample = useExample;
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

  public String getExamplePostfix() {
    return examplePostfix;
  }

  public void setExamplePostfix(String examplePostfix) {
    this.examplePostfix = examplePostfix;
  }

  public String getTablePrefix() {
    return tablePrefix;
  }

  public void setTablePrefix(String tablePrefix) {
    this.tablePrefix = tablePrefix;
  }

  public String getDomainPostfix() {
    return domainPostfix;
  }

  public void setDomainPostfix(String domainPostfix) {
    this.domainPostfix = domainPostfix;
  }

  public String getDefaultXmlPackage() {
    return defaultXmlPackage;
  }

  public void setDefaultXmlPackage(String defaultXmlPackage) {
    this.defaultXmlPackage = defaultXmlPackage;
  }

  public boolean isUseTableNameAlias() {
    return useTableNameAlias;
  }

  public void setUseTableNameAlias(boolean useTableNameAlias) {
    this.useTableNameAlias = useTableNameAlias;
  }

  public String getMgbTargetRuntime() {
    return mgbTargetRuntime;
  }

  public void setMgbTargetRuntime(String mgbTargetRuntime) {
    this.mgbTargetRuntime = mgbTargetRuntime;
  }

  public String getMgbJavaClientConfigType() {
    return mgbJavaClientConfigType;
  }

  public void setMgbJavaClientConfigType(String mgbJavaClientConfigType) {
    this.mgbJavaClientConfigType = mgbJavaClientConfigType;
  }

  public static GlobalConfig getDefault() {
    GlobalConfig globalConfig = new GlobalConfig();
    globalConfig.setSourcePath("src/main/java");
    globalConfig.setResourcePath("src/main/resources");
    globalConfig.setDomainPostfix("Entity");
    globalConfig.setMapperPostfix("Mapper");
    globalConfig.setExamplePostfix("Example");
    globalConfig.setUseExample(true);
    globalConfig.setComment(true);
    globalConfig.setDefaultXmlPackage("mybatis.mapper");
    globalConfig.setMgbTargetRuntime(MbgTargetRuntimeEnum.MY_BATIS3_DYNAMIC_SQL.getName());
    globalConfig.setMgbJavaClientConfigType(MbgJavaClientConfigTypeEnum.MIXEDMAPPER.getName());
    return globalConfig;
  }
}
