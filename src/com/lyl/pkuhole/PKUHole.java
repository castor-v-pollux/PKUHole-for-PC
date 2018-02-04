package com.lyl.pkuhole;

import java.util.Observable;

import com.lyl.pkuhole.gui.MainWindow;
import com.lyl.pkuhole.model.AttentionManager;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.utils.UIUtils;

public class PKUHole extends Observable {

	private static PKUHole instance = new PKUHole();

	private PKUHole() {
	}

	public static PKUHole getInstance() {
		return instance;
	}

	public User user;

	public void notifyUserChanged() {
		if (user != null)
			AttentionManager.getAttentionList();
		else
			AttentionManager.clearAttentionList();
		setChanged();
		notifyObservers(null);
	}

	public static void main(String[] arg) {
		UIUtils.setDefaultLookAndFeel();
		UIUtils.initGlobalFont();
		new MainWindow().init();
	}

}
