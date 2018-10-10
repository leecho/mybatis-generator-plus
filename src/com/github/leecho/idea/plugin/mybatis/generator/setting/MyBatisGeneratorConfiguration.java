package com.github.leecho.idea.plugin.mybatis.generator.setting;

import com.github.leecho.idea.plugin.mybatis.generator.model.EntityConfig;
import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.github.leecho.idea.plugin.mybatis.generator.model.GlobalConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


/**
 * 配置持久化
 */
@State(name = "MyBatisGeneratorConfiguration", storages = {@Storage("mybatis-generator-config.xml")})
public class MyBatisGeneratorConfiguration implements PersistentStateComponent<MyBatisGeneratorConfiguration> {

	private GlobalConfig globalConfig;
	private Map<String, Credential> credentials;
	private Map<String, EntityConfig> entityConfigs;

	@Nullable
	public static MyBatisGeneratorConfiguration getInstance(Project project) {
		return ServiceManager.getService(project, MyBatisGeneratorConfiguration.class);
	}

	@Override
	@Nullable
	public MyBatisGeneratorConfiguration getState() {
		return this;
	}

	@Override
	public void loadState(MyBatisGeneratorConfiguration myBatisGeneratorConfiguration) {
		XmlSerializerUtil.copyBean(myBatisGeneratorConfiguration, this);
	}

	public Map<String, Credential> getCredentials() {
		return credentials;
	}

	public void setCredentials(Map<String, Credential> credentials) {
		this.credentials = credentials;
	}

	public Map<String, EntityConfig> getEntityConfigs() {
		return entityConfigs;
	}

	public void setEntityConfigs(Map<String, EntityConfig> entityConfigs) {
		this.entityConfigs = entityConfigs;
	}

	public GlobalConfig getGlobalConfig() {
		if (this.globalConfig == null) {
			return GlobalConfig.getDefault();
		} else {
			return globalConfig;
		}
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}
}
