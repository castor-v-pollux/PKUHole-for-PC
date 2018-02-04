package com.lyl.pkuhole.tab;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.gui.LoginWindow;
import com.lyl.pkuhole.utils.UIUtils;

public class UserTab extends JPanel implements Observer {

	private static final Font LargeFont = new Font(Font.SANS_SERIF, Font.PLAIN, 24);

	private static final String UserGuestString = "当前用户：\n\t游客";

	private JTextArea text;
	private JButton button;

	public UserTab() {
		PKUHole.getInstance().addObserver(this);
		initComponent();
		initLayout();
		initEvent();
	}

	private void initComponent() {
		setBackground(Color.WHITE);

		text = new JTextArea(UserGuestString);
		text.setFont(LargeFont);
		text.setEditable(false);

		button = new JButton("登录");
		button.setFont(LargeFont);
	}

	private void initLayout() {
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gb);
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		// JTextArea text
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(20, 20, 20, 20);
		gb.setConstraints(text, gbc);
		add(text);
		// JButton button
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gb.setConstraints(button, gbc);
		add(button);
	}

	private void initEvent() {
		button.addActionListener(e -> {
			if (button.getText() == "登录")
				doLogin();
			else
				doLogout();
		});
	}

	private void doLogin() {
		new LoginWindow().init();
	}

	private void doLogout() {
		PKUHole.getInstance().user = null;
		PKUHole.getInstance().notifyUserChanged();
		UIUtils.messageBox("登出成功！");
	}

	@Override
	public void update(Observable o, Object arg) {
		if (PKUHole.getInstance().user == null) {
			text.setText(UserGuestString);
			button.setText("登录");
		} else {
			text.setText(PKUHole.getInstance().user.toFormattedString());
			button.setText("登出");
		}
	}
}
