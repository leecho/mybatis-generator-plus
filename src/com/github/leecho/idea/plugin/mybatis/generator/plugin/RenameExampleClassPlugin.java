package com.github.leecho.idea.plugin.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class RenameExampleClassPlugin extends PluginAdapter {
	private String target;

	/**
	 *
	 */
	public RenameExampleClassPlugin() {
	}

	@Override
	public boolean validate(List<String> warnings) {

		target = this.getProperties().getProperty("target");
		boolean valid = stringHasValue(target);

		if (!valid) {
			if (!stringHasValue(target)) {
				warnings.add(getString("ValidationError.18",
						"RenameExampleClassPlugin",
						"searchString"));
			}
		}

		return valid;
	}

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		introspectedTable.setExampleType(target);
	}
}
