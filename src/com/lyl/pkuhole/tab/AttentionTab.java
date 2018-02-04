package com.lyl.pkuhole.tab;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.model.AttentionManager;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.widgets.VerticalList;

public class AttentionTab extends JPanel implements Observer {

	// Components for header
	private JButton refresh;
	// Panel for header
	private JPanel header;
	// VerticalList for Topics
	private VerticalList topicList;
	private JScrollPane scrollPane;

	public AttentionTab() {
		PKUHole.getInstance().addObserver(this);
		initComponent();
		initLayout();
		initEvent();
		load();
	}

	public void initComponent() {
		refresh = new JButton("Ë¢ÐÂ");

		topicList = new VerticalList();
		scrollPane = new JScrollPane(topicList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	public void initLayout() {
		header = new JPanel();
		// Following set the layout of header.
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		header.setLayout(gb);
		// JButton refresh
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 40, 0, 40);
		gb.setConstraints(refresh, gbc);
		header.add(refresh);
		// Layout of header finished.
		setLayout(new BorderLayout());
		add(header, BorderLayout.SOUTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void initEvent() {
		refresh.addActionListener(e -> {
			AttentionManager.getAttentionList();
			topicList.removeAll();
			load();
		});
	}

	private void load() {
		List<Topic> topics = AttentionManager.topicList;
		if (topics == null)
			return;
		for (Topic topic : topics)
			topicList.addItem(topic.getCell(true));
		topicList.commit();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (PKUHole.getInstance().user == null) {
			topicList.removeAll();
		} else {
			load();
		}
	}

}
