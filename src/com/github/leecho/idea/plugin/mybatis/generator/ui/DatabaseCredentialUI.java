package com.github.leecho.idea.plugin.mybatis.generator.ui;

import com.github.leecho.idea.plugin.mybatis.generator.model.Credential;
import com.github.leecho.idea.plugin.mybatis.generator.util.StringUtils;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 账号密码输入界面
 * Created by kangtian on 2018/8/3.
 */
public class DatabaseCredentialUI extends DialogWrapper {

	private JPanel contentPanel = new JBPanel<>();

	private JTextField usernameField = new JBTextField(30);
	private JTextField passwordField = new JBPasswordField();
	private JLabel errorMessage = new JLabel("");
	private Credential credential;


	public DatabaseCredentialUI(Project project, Credential credential) throws HeadlessException {
		super(project);
		this.credential = credential;
		setTitle("Connect to Database");
		pack();

		contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		JPanel usernamePanel = new JBPanel<>();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		usernamePanel.setBorder(JBUI.Borders.empty(1));
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setPreferredSize(new Dimension(50, 25));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameField);
		contentPanel.add(usernamePanel);

		JPanel passwordPanel = new JBPanel<>();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.setBorder(JBUI.Borders.empty(1));
		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setPreferredSize(new Dimension(50, 25));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		contentPanel.add(passwordPanel);
		contentPanel.add(errorMessage);
		errorMessage.setForeground(JBColor.RED);
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
		credential.setUsername(usernameField.getText().trim());
		credential.setPwd(passwordField.getText().trim());
		super.doOKAction();
	}

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		return this.contentPanel;
	}
}
