package com.github.leecho.idea.plugin.mybatis.generator.generate;

import com.github.leecho.idea.plugin.mybatis.generator.model.ConnectionConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.TableConfig;
import com.github.leecho.idea.plugin.mybatis.generator.plugin.DbRemarksCommentGenerator;
import com.intellij.notification.*;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.github.leecho.idea.plugin.mybatis.generator.model.DbType;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import java.nio.charset.StandardCharsets;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成mybatis相关代码
 * Created by kangtian on 2018/7/28.
 */
public class MyBatisGenerateCommand {

	//持久化的配置
	private MyBatisGeneratorConfiguration myBatisGeneratorConfiguration;
	//界面默认配置
	private TableConfig tableConfig;
	private String username;
	//数据库类型
	private String databaseType;
	//数据库驱动
	private String driverClass;
	//数据库连接url
	private String url;

	public MyBatisGenerateCommand(TableConfig tableConfig) {
		this.tableConfig = tableConfig;
	}

	/**
	 * 自动生成的主逻辑
	 *
	 * @param project
	 * @param connectionConfig
	 * @throws Exception
	 */
	public void execute(Project project, ConnectionConfig connectionConfig) {
		this.myBatisGeneratorConfiguration = MyBatisGeneratorConfiguration.getInstance(project);

		saveConfig();//执行前 先保存一份当前配置

		driverClass = connectionConfig.getDriverClass();
		url = connectionConfig.getUrl();
		if (driverClass.contains("mysql")) {
			databaseType = "MySQL";
		} else if (driverClass.contains("oracle")) {
			databaseType = "Oracle";
		} else if (driverClass.contains("postgresql")) {
			databaseType = "PostgreSQL";
		} else if (driverClass.contains("sqlserver")) {
			databaseType = "SqlServer";
		} else if (driverClass.contains("sqlite")) {
			databaseType = "Sqlite";
		} else if (driverClass.contains("mariadb")) {
			databaseType = "MariaDB";
		}


		Configuration configuration = new Configuration();
		Context context = new Context(ModelType.CONDITIONAL);
		configuration.addContext(context);

		context.setId("myid");
		context.addProperty(PropertyRegistry.CONTEXT_AUTO_DELIMIT_KEYWORDS, "true");
		context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
		context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");
		context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, StandardCharsets.UTF_8.name());
		context.setTargetRuntime(tableConfig.getMgbTargetRuntime());

		JDBCConnectionConfiguration jdbcConfig = buildJdbcConfig();
		if (jdbcConfig == null) {
			return;
		}
		TableConfiguration tableConfig = buildTableConfig(context);
		JavaModelGeneratorConfiguration modelConfig = buildModelConfig();
		SqlMapGeneratorConfiguration mapperConfig = buildMapperXmlConfig();
		JavaClientGeneratorConfiguration daoConfig = buildMapperConfig();
		CommentGeneratorConfiguration commentConfig = buildCommentConfig();

		context.addTableConfiguration(tableConfig);
		context.setJdbcConnectionConfiguration(jdbcConfig);
		context.setJavaModelGeneratorConfiguration(modelConfig);
		context.setSqlMapGeneratorConfiguration(mapperConfig);
		context.setJavaClientGeneratorConfiguration(daoConfig);
		context.setCommentGeneratorConfiguration(commentConfig);
		addPluginConfiguration(context);

		createFolderForNeed(this.tableConfig);
		List<String> warnings = new ArrayList<>();
		// override=true
		ShellCallback shellCallback= new DefaultShellCallback(this.tableConfig.isOverride());
		Set<String> fullyQualifiedTables = new HashSet<>();
		Set<String> contexts = new HashSet<>();

		try {
			MyBatisGenerator myBatisCodeGenerator = new MyBatisGenerator(configuration, shellCallback, warnings);
			StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
			Balloon balloon = JBPopupFactory.getInstance()
					.createHtmlTextBalloonBuilder("Generating Code...", MessageType.INFO, null)
					.createBalloon();
			balloon.show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);

			Task.Backgroundable generateTask = new Task.Backgroundable(project, "Generating MyBatis Code", false) {
				@Override
				public void run(ProgressIndicator indicator) {
					indicator.setText("Generating MyBatis Code");
					indicator.setFraction(0.0);
					indicator.setIndeterminate(true);
					try {
						myBatisCodeGenerator.generate(new GenerateCallback(indicator, balloon), contexts, fullyQualifiedTables);
						VirtualFile baseDir = project.getBaseDir();
						baseDir.refresh(false, true);

						NotificationGroup balloonNotifications = new NotificationGroup("Mybatis Generator Plus", NotificationDisplayType.STICKY_BALLOON, true);

						List<String> result = myBatisCodeGenerator.getGeneratedJavaFiles().stream()
								.map(generatedJavaFile -> String.format("<a href=\"%s%s/%s/%s\" target=\"_blank\">%s</a>", getRelativePath(project), MyBatisGenerateCommand.this.tableConfig.getSourcePath(), generatedJavaFile.getTargetPackage().replace(".", File.separator), generatedJavaFile.getFileName(), generatedJavaFile.getFileName()))
								.collect(Collectors.toList());
						result.addAll(myBatisCodeGenerator.getGeneratedXmlFiles().stream()
								.map(generatedXmlFile -> String.format("<a href=\"%s%s/%s/%s\" target=\"_blank\">%s</a>", getRelativePath(project).replace(project.getBasePath() + File.separator, ""), MyBatisGenerateCommand.this.tableConfig.getResourcePath(), generatedXmlFile.getTargetPackage().replace(".", File.separator), generatedXmlFile.getFileName(), generatedXmlFile.getFileName()))
								.collect(Collectors.toList()));
						Notification notification = balloonNotifications.createNotification("Generate Successfully", "<html>" + String.join("<br/>", result) + "</html>", NotificationType.INFORMATION, (notification1, hyperlinkEvent) -> {
							if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
								new OpenFileDescriptor(project, Objects.requireNonNull(project.getBaseDir().findFileByRelativePath(hyperlinkEvent.getDescription()))).navigate(true);
							}
						});
						Notifications.Bus.notify(notification);
					} catch (Exception e) {
						e.printStackTrace();
						balloon.hide();
						Notification notification = new Notification("Mybatis Generator Plus", "", NotificationType.ERROR);
						notification.setTitle("Generate Failed");
						notification.setContent("Cause:" + e.getMessage());
						Notifications.Bus.notify(notification);
					}
				}
			};
			generateTask.setCancelText("Stop generate code").queue();
			generateTask.setCancelTooltipText("Stop generate mybatis code");

		} catch (Exception e) {
			e.printStackTrace();
			Messages.showMessageDialog(e.getMessage(), "Generate Failure", Messages.getInformationIcon());
		}

	}

	private String getRelativePath(Project project) {
		if (tableConfig.getModuleRootPath().equals(project.getBasePath())) {
			return "";
		} else {
			return tableConfig.getModuleRootPath().replace(project.getBasePath() + File.separator, "") + File.separator;
		}
	}


	/**
	 * 创建所需目录
	 *
	 * @param tableConfig
	 */
	private void createFolderForNeed(TableConfig tableConfig) {


		String sourcePath = tableConfig.getModuleRootPath() + File.separator + tableConfig.getSourcePath() + File.separator;
		String resourcePath = tableConfig.getModuleRootPath() + File.separator + tableConfig.getResourcePath() + File.separator;

		File sourceFile = new File(sourcePath);
		if (!sourceFile.exists() && !sourceFile.isDirectory()) {
			sourceFile.mkdirs();
		}

		File resourceFile = new File(resourcePath);
		if (!resourceFile.exists() && !resourceFile.isDirectory()) {
			resourceFile.mkdirs();
		}
	}


	/**
	 * 保存当前配置到历史记录
	 */
	private void saveConfig() {
		Map<String, TableConfig> historyConfigList = myBatisGeneratorConfiguration.getTableConfigs();
		if (historyConfigList == null) {
			historyConfigList = new HashMap<>();
		}
		historyConfigList.put(tableConfig.getName(), tableConfig);
		myBatisGeneratorConfiguration.setTableConfigs(historyConfigList);
	}

	/**
	 * 生成数据库连接配置
	 *
	 * @return
	 */
	private JDBCConnectionConfiguration buildJdbcConfig() {

		JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
		jdbcConfig.addProperty("nullCatalogMeansCurrent", "true");
		jdbcConfig.addProperty("remarks","true");
		jdbcConfig.addProperty("useInformationSchema","true");

		Map<String, Credential> users = myBatisGeneratorConfiguration.getCredentials();
		Credential credential = users.get(url);

		username = credential.getUsername();


		jdbcConfig.setUserId(username);
		jdbcConfig.setPassword(credential.getPwd());

		Boolean mySQL_8 = tableConfig.isMysql8();
		if (mySQL_8) {
			driverClass = DbType.MySQL_8.getDriverClass();
			url += "?serverTimezone=UTC&useSSL=false";
		} else {
			url += "?useSSL=false";
		}

		jdbcConfig.setDriverClass(driverClass);
		jdbcConfig.setConnectionURL(url);
		return jdbcConfig;
	}

	/**
	 * 生成table配置
	 *
	 * @param context
	 * @return
	 */
	private TableConfiguration buildTableConfig(Context context) {
		TableConfiguration tableConfig = new TableConfiguration(context);
		tableConfig.setTableName(this.tableConfig.getTableName());
		tableConfig.setDomainObjectName(this.tableConfig.getDomainName());

		String schema;
		if (databaseType.equals(DbType.MySQL.name())) {
			String[] name_split = url.split(File.separator);
			schema = name_split[name_split.length - 1];
			tableConfig.setSchema(schema);
		} else if (databaseType.equals(DbType.Oracle.name())) {
			String[] name_split = url.split(":");
			schema = name_split[name_split.length - 1];
			tableConfig.setCatalog(schema);
		} else {
			String[] name_split = url.split(File.separator);
			schema = name_split[name_split.length - 1];
			tableConfig.setCatalog(schema);
		}

		if (!this.tableConfig.isUseExample()) {
			tableConfig.setUpdateByExampleStatementEnabled(false);
			tableConfig.setCountByExampleStatementEnabled(false);
			tableConfig.setDeleteByExampleStatementEnabled(false);
			tableConfig.setSelectByExampleStatementEnabled(false);
		}

		if (this.tableConfig.isUseSchemaPrefix()) {
			if (DbType.MySQL.name().equals(databaseType)) {
				tableConfig.setSchema(schema);
			} else if (DbType.Oracle.name().equals(databaseType)) {
				//Oracle的schema为用户名，如果连接用户拥有dba等高级权限，若不设schema，会导致把其他用户下同名的表也生成一遍导致mapper中代码重复
				tableConfig.setSchema(username);
			} else {
				tableConfig.setCatalog(schema);
			}
		}

		if(this.tableConfig.getColumnSettings().size() > 0){
			this.tableConfig.getColumnSettings().forEach((key, value) -> {
				if(value.getIgnore()){
					tableConfig.addIgnoredColumn(new IgnoredColumn(value.getColumn()));
				}	else{
					ColumnOverride override = new ColumnOverride(value.getColumn());
					override.setJavaProperty(value.getJavaProperty());
					override.setJavaType(value.getJavaType());
					override.setJdbcType(value.getJdbcType());
					tableConfig.addColumnOverride(override);
				}

			});
		}

		if (DbType.PostgreSQL.getDriverClass().equals(driverClass)) {
			tableConfig.setDelimitIdentifiers(true);
		}

		if (!StringUtils.isEmpty(this.tableConfig.getPrimaryKey())) {
			String dbType = databaseType;
			if (DbType.MySQL.name().equals(databaseType)) {
				dbType = "JDBC";
				//dbType为JDBC，且配置中开启useGeneratedKeys时，Mybatis会使用Jdbc3KeyGenerator,
				//使用该KeyGenerator的好处就是直接在一次INSERT 语句内，通过resultSet获取得到 生成的主键值，
				//并很好的支持设置了读写分离代理的数据库
				//例如阿里云RDS + 读写分离代理 无需指定主库
				//当使用SelectKey时，Mybatis会使用SelectKeyGenerator，INSERT之后，多发送一次查询语句，获得主键值
				//在上述读写分离被代理的情况下，会得不到正确的主键
			}
			tableConfig.setGeneratedKey(new GeneratedKey(this.tableConfig.getPrimaryKey(), dbType, true, "post"));
		}

		if (this.tableConfig.isUseActualColumnNames()) {
			tableConfig.addProperty(PropertyRegistry.TABLE_USE_ACTUAL_COLUMN_NAMES, "true");
		}

		if (this.tableConfig.isUseTableNameAlias()) {
			tableConfig.setAlias(this.tableConfig.getTableName());
		}
		tableConfig.setMapperName(this.tableConfig.getMapperName());
		return tableConfig;
	}


	/**
	 * 生成实体类配置
	 *
	 * @return
	 */
	private JavaModelGeneratorConfiguration buildModelConfig() {
		String projectFolder = tableConfig.getModuleRootPath();
		String modelPackage = tableConfig.getDomainPackage();
		String sourcePath = tableConfig.getSourcePath();

		JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();

		if (!StringUtils.isEmpty(modelPackage)) {
			modelConfig.setTargetPackage(modelPackage);
		} else {
			modelConfig.setTargetPackage("");
		}
		modelConfig.setTargetProject(projectFolder + File.separator + sourcePath + File.separator);
		return modelConfig;
	}

	/**
	 * 生成mapper.xml文件配置
	 *
	 * @return
	 */
	private SqlMapGeneratorConfiguration buildMapperXmlConfig() {

		String projectFolder = tableConfig.getModuleRootPath();
		String mappingXMLPackage = tableConfig.getXmlPackage();
		String resourcePath = tableConfig.getResourcePath();

		SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();

		if (!StringUtils.isEmpty(mappingXMLPackage)) {
			mapperConfig.setTargetPackage(mappingXMLPackage);
		} else {
			mapperConfig.setTargetPackage("");
		}

		mapperConfig.setTargetProject(projectFolder + File.separator + resourcePath + File.separator);

		return mapperConfig;
	}

	/**
	 * 生成dao接口文件配置
	 *
	 * @return
	 */
	private JavaClientGeneratorConfiguration buildMapperConfig() {

		String projectFolder = tableConfig.getModuleRootPath();
		String mapperPackage = tableConfig.getMapperPackage();
		String mapperPath = tableConfig.getSourcePath();

		JavaClientGeneratorConfiguration mapperConfig = new JavaClientGeneratorConfiguration();
		mapperConfig.setConfigurationType(tableConfig.getMgbJavaClientConfigType());
		mapperConfig.setTargetPackage(mapperPackage);

		if (!StringUtils.isEmpty(mapperPackage)) {
			mapperConfig.setTargetPackage(mapperPackage);
		} else {
			mapperConfig.setTargetPackage("");
		}

		mapperConfig.setTargetProject(projectFolder + File.separator + mapperPath + File.separator);

		return mapperConfig;
	}

	/**
	 * 生成注释配置
	 *
	 * @return
	 */
	private CommentGeneratorConfiguration buildCommentConfig() {
		CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
		if (tableConfig.isComment()) {
			commentConfig.addProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS, "true");
			commentConfig.addProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss");
		}
		if (tableConfig.isAnnotation()) {
			commentConfig.addProperty("annotations", "true");
		}

		return commentConfig;
	}

	/**
	 * 添加相关插件（注意插件文件需要通过jar引入）
	 *
	 * @param context
	 */
	private void addPluginConfiguration(Context context) {


		//实体添加序列化
		PluginConfiguration serializablePlugin = new PluginConfiguration();
		serializablePlugin.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
		serializablePlugin.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
		context.addPluginConfiguration(serializablePlugin);


		if (tableConfig.isNeedToStringHashcodeEquals()) {
			PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
			equalsHashCodePlugin.addProperty("type", "org.mybatis.generator.plugins.EqualsHashCodePlugin");
			equalsHashCodePlugin.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
			context.addPluginConfiguration(equalsHashCodePlugin);
			PluginConfiguration toStringPluginPlugin = new PluginConfiguration();
			toStringPluginPlugin.addProperty("type", "org.mybatis.generator.plugins.ToStringPlugin");
			toStringPluginPlugin.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
			context.addPluginConfiguration(toStringPluginPlugin);
		}

		if (tableConfig.isLombokAnnotation()) {
			PluginConfiguration lombokPlugin = new PluginConfiguration();
			lombokPlugin.addProperty("type", "com.github.leecho.idea.plugin.mybatis.generator.plugin.LombokPlugin");
			lombokPlugin.setConfigurationType("com.github.leecho.idea.plugin.mybatis.generator.plugin.LombokPlugin");
			if (tableConfig.isLombokBuilderAnnotation()) {
				lombokPlugin.addProperty("builder", "true");
				lombokPlugin.addProperty("allArgsConstructor", "true");
				lombokPlugin.addProperty("noArgsConstructor", "true");
			}
			context.addPluginConfiguration(lombokPlugin);
		}

		if (tableConfig.isUseExample()) {
			PluginConfiguration renameExamplePlugin = new PluginConfiguration();
			renameExamplePlugin.addProperty("type", "com.github.leecho.idea.plugin.mybatis.generator.plugin.RenameExampleClassPlugin");
			renameExamplePlugin.setConfigurationType("com.github.leecho.idea.plugin.mybatis.generator.plugin.RenameExampleClassPlugin");
			renameExamplePlugin.addProperty("target", tableConfig.getExamplePackage() + "." + tableConfig.getExampleName());
			context.addPluginConfiguration(renameExamplePlugin);
		}

		JavaTypeResolverConfiguration javaTypeResolverPlugin = new JavaTypeResolverConfiguration();
		javaTypeResolverPlugin.setConfigurationType("com.github.leecho.idea.plugin.mybatis.generator.plugin.MyJavaTypeResolverDefaultImpl");
		javaTypeResolverPlugin.addProperty(PropertyRegistry.TYPE_RESOLVER_FORCE_BIG_DECIMALS,"true");
		//for JSR310
		if (tableConfig.isJsr310Support()) {
			javaTypeResolverPlugin.addProperty(PropertyRegistry.TYPE_RESOLVER_USE_JSR310_TYPES,"true");
		}
		context.setJavaTypeResolverConfiguration(javaTypeResolverPlugin);

		//repository 插件
		if (tableConfig.isAnnotationDAO()) {
			if (DbType.MySQL.name().equals(databaseType)
					|| DbType.PostgreSQL.name().equals(databaseType)) {
				PluginConfiguration repositoryPlugin = new PluginConfiguration();
				repositoryPlugin.addProperty("type", "com.github.leecho.idea.plugin.mybatis.generator.plugin.RepositoryPlugin");
				repositoryPlugin.setConfigurationType("com.github.leecho.idea.plugin.mybatis.generator.plugin.RepositoryPlugin");
				context.addPluginConfiguration(repositoryPlugin);
			}
		}

		//13
		if (tableConfig.isUseDAOExtendStyle()) {
			if (DbType.MySQL.name().equals(databaseType)
					|| DbType.PostgreSQL.name().equals(databaseType)) {
				PluginConfiguration commonDAOInterfacePlugin = new PluginConfiguration();
				commonDAOInterfacePlugin.addProperty("type", "com.github.leecho.idea.plugin.mybatis.generator.plugin.CommonDAOInterfacePlugin");
				commonDAOInterfacePlugin.setConfigurationType("com.github.leecho.idea.plugin.mybatis.generator.plugin.CommonDAOInterfacePlugin");
				context.addPluginConfiguration(commonDAOInterfacePlugin);
			}
		}

	}
}
