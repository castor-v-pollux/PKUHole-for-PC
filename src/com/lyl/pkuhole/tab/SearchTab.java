package com.lyl.pkuhole.tab;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.apache.http.util.TextUtils;

import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.network.Network;
import com.lyl.pkuhole.utils.UIUtils;
import com.lyl.pkuhole.widgets.JVerticalList;

import io.reactivex.schedulers.Schedulers;

public class SearchTab extends JPanel {

	private static final int MAX_PAGE_SIZE = 100;

	private static final String[] SearchHintString = new String[] { "搜索内容：", "树洞号：#" };
	private static final String[] SearchTypeString = new String[] { "根据内容搜索", "根据树洞号搜索" };

	// Components for header
	private JLabel searchHint;
	private JTextField searchText;
	private JComboBox<String> searchType;
	private JButton search;
	// Panel for header
	private JPanel header;
	// VerticalList for Topics
	private JVerticalList topicList;
	private JScrollPane scrollPane;

	public SearchTab() {
		initComponent();
		initLayout();
		initEvent();
	}

	private void initComponent() {
		searchHint = new JLabel("搜索内容：");
		searchText = new JTextField();
		searchType = new JComboBox<String>(SearchTypeString);
		search = new JButton("搜索");

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
		// JLabel searchHint
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gb.setConstraints(searchHint, gbc);
		header.add(searchHint);
		// JTextField searchText
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gb.setConstraints(searchText, gbc);
		header.add(searchText);
		// JComboBox searchType
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 40, 0, 0);
		gb.setConstraints(searchType, gbc);
		header.add(searchType);
		// JButton search
		gb.setConstraints(search, gbc);
		header.add(search);
		// Layout of header finished.
		setLayout(new BorderLayout());
		add(header, BorderLayout.SOUTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void initEvent() {
		searchType.addActionListener(e -> {
			searchHint.setText(SearchHintString[searchType.getSelectedIndex()]);
		});
		search.addActionListener(e -> {
			String s = searchText.getText().trim();
			if (TextUtils.isEmpty(s)) {
				UIUtils.messageBox("请输入搜索内容！");
				return;
			}
			if (searchType.getSelectedIndex() == 0) {
				searchByText(s);
			} else {
				searchByPid(s);
			}
		});
	}

	private void searchByText(String s) {
		Network.searchTopics(s, MAX_PAGE_SIZE)
				.observeOn(Schedulers.io())
				.subscribe(topics -> {
					if (topics == null || topics.length == 0) {
						UIUtils.messageBox("无搜索结果！");
						return;
					}
					topicList.removeAll();
					for (Topic topic : topics)
						topicList.addItem(topic.getCell(true));
					topicList.commit();
					if (topics.length == MAX_PAGE_SIZE)
						UIUtils.messageBox("搜索结果过多！只显示最近100条。");
				}, err -> {
					UIUtils.messageBox("搜索失败！原因：" + err.getMessage());
				});
	}

	private void searchByPid(String s) {
		int pid;
		try {
			pid = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			UIUtils.messageBox("请输入数字！");
			return;
		}
		Network.getSingleTopic(pid)
				.observeOn(Schedulers.io())
				.subscribe(topic -> {
					if (topic == null) {
						UIUtils.messageBox("无搜索结果！");
						return;
					}
					topicList.removeAll();
					topicList.addItem(topic.getCell(true));
					topicList.commit();
				}, err -> {
					UIUtils.messageBox("搜索失败！原因：" + err.getMessage());
				});
	}

}
