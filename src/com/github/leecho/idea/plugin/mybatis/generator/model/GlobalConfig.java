package com.github.leecho.idea.plugin.mybatis.generator.model;

public class GlobalConfig {

	private String moduleRootPath;
	private String sourcePath;
	private String resourcePath;
	private String mapperPostfix;
	private String examplePostfix;

	private String domainPackage;
	private String mapperPackage;
	private String examplePackage;
	private String xmlPackage;


	/**
	 * 是否分页
	 */
	private boolean offsetLimit;

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
	 * 是否select 增加ForUpdate
	 */
	private boolean needForUpdate;

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

	private boolean swaggerAnnotation;

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

	public boolean isOffsetLimit() {
		return offsetLimit;
	}

	public void setOffsetLimit(boolean offsetLimit) {
		this.offsetLimit = offsetLimit;
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

	public boolean isNeedForUpdate() {
		return needForUpdate;
	}

	public void setNeedForUpdate(boolean needForUpdate) {
		this.needForUpdate = needForUpdate;
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

	public boolean isUseTableNameAlias() {
		return useTableNameAlias;
	}

	public void setUseTableNameAlias(boolean useTableNameAlias) {
		this.useTableNameAlias = useTableNameAlias;
	}

	public boolean isUseExample() {
		return useExample;
	}

	public void setUseExample(boolean useExample) {
		this.useExample = useExample;
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

	public String getModuleRootPath() {
		return moduleRootPath;
	}

	public void setModuleRootPath(String moduleRootPath) {
		this.moduleRootPath = moduleRootPath;
	}

	public String getExamplePostfix() {
		return examplePostfix;
	}

	public void setExamplePostfix(String examplePostfix) {
		this.examplePostfix = examplePostfix;
	}

	public String getExamplePackage() {
		return examplePackage;
	}

	public void setExamplePackage(String examplePackage) {
		this.examplePackage = examplePackage;
	}

	public boolean isSwaggerAnnotation() {
		return swaggerAnnotation;
	}

	public void setSwaggerAnnotation(boolean swaggerAnnotation) {
		this.swaggerAnnotation = swaggerAnnotation;
	}

	public static GlobalConfig getDefault() {
		GlobalConfig globalConfig = new GlobalConfig();
		globalConfig.setSourcePath("src/main/java");
		globalConfig.setResourcePath("src/main/resources");
		globalConfig.setMapperPostfix("Mapper");
		globalConfig.setXmlPackage("mapper");
		globalConfig.setExamplePostfix("Example");
		globalConfig.setUseExample(true);
		globalConfig.setComment(true);
		return globalConfig;
	}
}
