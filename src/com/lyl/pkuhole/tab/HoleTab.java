package com.lyl.pkuhole.tab;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.network.Network;
import com.lyl.pkuhole.utils.UIUtils;
import com.lyl.pkuhole.widgets.JVerticalList;

import io.reactivex.schedulers.Schedulers;

public class HoleTab extends JPanel {

	private static final int MAX_PAGE_NUM = 100000;

	// Components for header
	private JButton left, right, go, refresh;
	private JLabel page;
	private JSpinner spinner;
	// Container for JButton left, JLabel page and JButton right.
	private JPanel panel;
	// Panel for header
	private JPanel header;
	// VerticalList for Topics
	private JVerticalList topicList;
	private JScrollPane scrollPane;

	private int pageNum;

	public HoleTab() {
		initComponent();
		initLayout();
		initEvent();
		setPageNum(1);
	}

	private void initComponent() {
		left = new JButton("��");
		right = new JButton("��");
		go = new JButton("��ת");
		refresh = new JButton("ˢ��");
		page = new JLabel("��" + pageNum + "ҳ");
		page.setHorizontalAlignment(SwingConstants.CENTER);
		spinner = new JSpinner(new SpinnerNumberModel(1, 1, MAX_PAGE_NUM, 1));

		panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(gb);
		gbc.fill = GridBagConstraints.NONE;
		gb.setConstraints(left, gbc);
		panel.add(left);
		gbc.insets = new Insets(0, 40, 0, 40);
		gb.setConstraints(page, gbc);
		panel.add(page);
		gbc.insets = new Insets(0, 0, 0, 0);
		gb.setConstraints(right, gbc);
		panel.add(right);

		topicList = new JVerticalList();
		scrollPane = new JScrollPane(topicList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	private void initLayout() {
		header = new JPanel();
		// Following set the layout of header.
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		header.setLayout(gb);
		gbc.fill = GridBagConstraints.NONE;
		// JButton fresh
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 40, 0, 40);
		gb.setConstraints(refresh, gbc);
		header.add(refresh);
		// JPanel panel
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(panel, gbc);
		header.add(panel);
		// JSpinner spinner
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(spinner, gbc);
		header.add(spinner);
		// JButton go
		gbc.insets = new Insets(0, 0, 0, 40);
		gb.setConstraints(go, gbc);
		header.add(go);
		// Layout of header finished.
		setLayout(new BorderLayout());
		add(header, BorderLayout.SOUTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void initEvent() {
		refresh.addActionListener(e -> loadPage(pageNum));
		left.addActionListener(e -> setPageNum(pageNum - 1));
		right.addActionListener(e -> setPageNum(pageNum + 1));
		go.addActionListener(e -> setPageNum((int) spinner.getValue()));
	}

	private void setPageNum(int newPageNum) {
		if (newPageNum == pageNum)
			return;
		loadPage(newPageNum);
	}

	private void loadPage(int pageNum) {
		Network.getTopics(pageNum)
				.observeOn(Schedulers.io())
				.subscribe(topics -> {
					if (topics == null || topics.length == 0) {
						UIUtils.messageBox("����ʧ�ܣ���ҳ��Ϊ�գ�");
						return;
					}
					topicList.removeAll();
					for (Topic topic : topics) {
						topicList.addItem(topic.getCell(true));
					}
					topicList.commit();
					page.setText("��" + pageNum + "ҳ");
					if (pageNum == 1)
						left.setEnabled(false);
					else
						left.setEnabled(true);
					if (pageNum == MAX_PAGE_NUM)
						right.setEnabled(false);
					else
						right.setEnabled(true);
					HoleTab.this.pageNum = pageNum;
				}, err -> {
					UIUtils.messageBox("����ҳ��ʧ�ܣ�ԭ��" + err.getMessage());
				});
	}

}
