package com.lyl.pkuhole.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.tab.AttentionTab;
import com.lyl.pkuhole.tab.HoleTab;
import com.lyl.pkuhole.tab.PostTab;
import com.lyl.pkuhole.tab.SearchTab;
import com.lyl.pkuhole.tab.UserTab;

public class MainWindow extends JFrame implements Observer {

	private JTabbedPane tabbedPane;

	private Component[] tabs;

	private static final String[] tabNames = new String[] { "发表", "树洞", "关注", "搜索", "用户" };

	public void init() {
		PKUHole.getInstance().addObserver(this);
		tabs = new Component[5];
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
		for (int i = 0; i < 5; i++) {
			tabbedPane.addTab(tabNames[i], null);
		}
		tabbedPane.setFont(new Font(Font.SANS_SERIF, 0, 24));
		add(tabbedPane, BorderLayout.CENTER);
		invalidateUser();
		tabbedPane.addChangeListener(e -> {
			if (tabbedPane.getSelectedComponent() == null) {
				loadTab(tabbedPane.getSelectedIndex());
			}
		});
		tabbedPane.setSelectedIndex(1);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1000, 600));
		setPreferredSize(new Dimension(1000, 800));
		pack();
		setVisible(true);
	}

	private void loadTab(int n) {
		switch (n) {
		case 0:
			tabs[0] = new PostTab();
			break;
		case 1:
			tabs[1] = new HoleTab();
			break;
		case 2:
			tabs[2] = new AttentionTab();
			break;
		case 3:
			tabs[3] = new SearchTab();
			break;
		case 4:
			tabs[4] = new UserTab();
			break;
		}
		tabbedPane.setComponentAt(n, tabs[n]);
	}

	public void invalidateUser() {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setEnabledAt(2, false);
			tabs[0] = null;
			tabs[2] = null;
			setTitle("P大树洞 - 游客");
		} else {
			tabbedPane.setEnabledAt(0, true);
			tabbedPane.setEnabledAt(2, true);
			setTitle(String.format("P大树洞 - %s[%s]", user.name, user.department));
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		invalidateUser();
	}

}
