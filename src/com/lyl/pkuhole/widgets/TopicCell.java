package com.lyl.pkuhole.widgets;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.lyl.pkuhole.gui.TopicWindow;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.model.TopicType;

public class TopicCell extends JTextArea implements VerticalList.ListItemListener {

	private static final Font BorderTitleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);

	private static final Color SelectedColor = new Color(0xff, 0xff, 0xcc);

	private TitledBorder border;

	private Topic topic;

	private boolean isNewWindowEnabled;

	public TopicCell(Topic topic, boolean isNewWindowEnabled) {
		this.topic = topic;
		this.isNewWindowEnabled = isNewWindowEnabled;
		init();
	}

	private void init() {
		setEditable(false);
		setLineWrap(true);

		setFormattedText();

		border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.ORANGE, 5), null,
				TitledBorder.LEFT, TitledBorder.TOP, BorderTitleFont);
		border.setTitle("#" + topic.pid);
		setBorder(border);
	}

	public void refresh() {
		setFormattedText();
	}

	private void setFormattedText() {
		if (topic.type == TopicType.AUDIO)
			setText("[这是一条语音树洞，目前暂不支持]\n" + topic.toFormattedString());
		else
			setText(topic.toFormattedString());
	}

	@Override
	public void onSelected(boolean isSelected) {
		setBackground(isSelected ? SelectedColor : Color.WHITE);
	}

	@Override
	public void onClicked() {
		if (isNewWindowEnabled) {
			new TopicWindow(topic).init();
		} else {
			/*
			 * Do comment. Delegated to listener in TopicWindow and do nothing here.
			 */
		}
	}

}
