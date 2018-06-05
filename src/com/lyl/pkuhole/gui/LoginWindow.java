package com.lyl.pkuhole.gui;

import java.awt.Container;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.http.util.TextUtils;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.network.Network;
import com.lyl.pkuhole.utils.UIUtils;

import io.reactivex.schedulers.Schedulers;

public class LoginWindow extends JDialog {

	private JLabel userNameHint, passwordHint;
	private JTextField userName;
	private JPasswordField password;
	private JButton confirm, cancel;
	private JPanel userNamePanel, passwordPanel, buttonPanel;

	public LoginWindow() {
		super((JFrame) null, "�û���¼", true);
	}

	public void init() {
		initComponent();
		initLayout();
		initEvent();
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void initComponent() {
		userNameHint = new JLabel("ѧ�ţ�");
		passwordHint = new JLabel("���룺");

		userName = new JTextField(20);
		password = new JPasswordField(20);
		password.setEchoChar('��');

		confirm = new JButton("��¼");
		cancel = new JButton("ȡ��");
	}

	private void initLayout() {
		// userNamePanel
		userNamePanel = new JPanel();
		userNamePanel.setLayout(new BoxLayout(userNamePanel, BoxLayout.X_AXIS));
		userNamePanel.add(Box.createHorizontalStrut(40));
		userNamePanel.add(userNameHint);
		userNamePanel.add(userName);
		userNamePanel.add(Box.createHorizontalStrut(40));
		// passwordPanel
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.add(Box.createHorizontalStrut(40));
		passwordPanel.add(passwordHint);
		passwordPanel.add(password);
		passwordPanel.add(Box.createHorizontalStrut(40));
		// buttonPanel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalStrut(40));
		buttonPanel.add(confirm);
		buttonPanel.add(Box.createHorizontalStrut(40));
		buttonPanel.add(cancel);
		buttonPanel.add(Box.createHorizontalStrut(40));
		// JDialog
		Container c = getContentPane();
		c.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		c.add(Box.createVerticalStrut(40));
		c.add(userNamePanel);
		c.add(Box.createVerticalStrut(40));
		c.add(passwordPanel);
		c.add(Box.createVerticalStrut(40));
		c.add(buttonPanel);
		c.add(Box.createVerticalStrut(40));

		setResizable(false);
		setLocationByPlatform(true);
	}

	private void initEvent() {
		confirm.addActionListener(e -> {
			String userName = this.userName.getText();
			String password = new String(this.password.getPassword());
			if (TextUtils.isEmpty(userName)) {
				UIUtils.messageBox("������ѧ�ţ�");
				return;
			}
			if (TextUtils.isEmpty(password)) {
				UIUtils.messageBox("���������룡");
				return;
			}
			Network.login(userName, password)
					.observeOn(Schedulers.io())
					.subscribe(user -> {
						user.id = Long.parseLong(userName);
						PKUHole.getInstance().user = user;
						PKUHole.getInstance().notifyUserChanged();
						dispose();
					}, err -> {
						UIUtils.messageBox("��¼ʧ�ܣ�ԭ��" + err.getMessage());// TODO
					});
		});
		cancel.addActionListener(e -> {
			dispose();
		});
	}

}
