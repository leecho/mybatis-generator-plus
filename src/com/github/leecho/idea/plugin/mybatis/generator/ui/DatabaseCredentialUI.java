package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.contants.PluginContants;
import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.github.leecho.idea.plugin.mybatis.generator.setting.MyBatisGeneratorConfiguration;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.util.ui.JBUI;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 账号密码输入界面
 * Created by kangtian on 2018/8/3.
 */
public class DatabaseCredentialUI extends DialogWrapper {

	private MyBatisGeneratorConfiguration myBatisGeneratorConfiguration;
	private String url;
	private Project project;
	private JPanel contentPanel = new JBPanel<>();

	private JTextField usernameField = new JBTextField(20);
	private JTextField passwordField = new JBPasswordField();
	private JLabel errorMessage = new JLabel("");


	public DatabaseCredentialUI(Project project, String url) throws HeadlessException {
		super(project);
		this.url = url;
		this.project = project;
		this.myBatisGeneratorConfiguration = MyBatisGeneratorConfiguration.getInstance(project);
		setTitle("Database Credential");
		pack();

		contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		Map<String, Credential> credentials = myBatisGeneratorConfiguration.getCredentials();

		JPanel usernamePanel = new JBPanel<>();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		usernamePanel.setBorder(JBUI.Borders.empty(2));
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setPreferredSize(new Dimension(100, 20));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameField);
		if(credentials != null && credentials.containsKey(url)){
			usernameField.setText(credentials.get(url).getUsername());
		}

		JPanel passwordPanel = new JBPanel<>();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.setBorder(JBUI.Borders.empty(2));
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setPreferredSize(new Dimension(100, 20));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		contentPanel.add(usernamePanel);
		contentPanel.add(passwordPanel);
		contentPanel.add(errorMessage);
		errorMessage.setForeground(Color.RED);
		this.init();
	}

	@Override
	protected void doOKAction() {

		if (StringUtils.isEmpty(usernameField.getText())) {
			errorMessage.setText("Username must not be null");
			return;
		}

		if (StringUtils.isEmpty(passwordField.getText())) {
			errorMessage.setText("Password must not be null");
			return;
		}

		Map<String, Credential> credentials = myBatisGeneratorConfiguration.getCredentials();
		if (credentials == null) {
			credentials = new HashMap<>();
		}
		credentials.put(url, new Credential(usernameField.getText()));
		CredentialAttributes attributes = new CredentialAttributes(PluginContants.PLUGIN_NAME + "-" + url, usernameField.getText(), this.getClass(), false);
		Credentials saveCredentials = new Credentials(attributes.getUserName(), passwordField.getText());
		PasswordSafe.getInstance().set(attributes, saveCredentials);
		myBatisGeneratorConfiguration.setCredentials(credentials);
		VirtualFile baseDir = project.getBaseDir();
		baseDir.refresh(false, true);

		super.doOKAction();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return this.contentPanel;
	}
}
