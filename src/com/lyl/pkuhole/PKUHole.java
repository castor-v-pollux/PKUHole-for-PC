package com.lyl.pkuhole;

import java.util.Observable;

import com.lyl.pkuhole.gui.MainWindow;
import com.lyl.pkuhole.model.AttentionManager;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.network.Network;
import com.lyl.pkuhole.utils.UIUtils;

import io.reactivex.schedulers.Schedulers;

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
			AttentionManager.getAttentionList()
					.observeOn(Schedulers.io())
					.subscribe(topics -> {
						setChanged();
						notifyObservers(null);
					}, err -> {
						UIUtils.messageBox("��ȡ��ע�б�ʧ�ܣ�ԭ��" + err.getMessage() + "\n�����´򿪱��ͻ��ˡ�");
						System.exit(0);
					});
		else {
			AttentionManager.clearAttentionList();
			setChanged();
			notifyObservers(null);
		}
	}

	public static void main(String[] arg) {
		UIUtils.setDefaultLookAndFeel();
		UIUtils.initGlobalFont();
		Network.init();
		new MainWindow().init();
	}

}
