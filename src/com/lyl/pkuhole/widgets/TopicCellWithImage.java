package com.lyl.pkuhole.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.lyl.pkuhole.gui.ImageWindow;
import com.lyl.pkuhole.gui.TopicWindow;
import com.lyl.pkuhole.model.Topic;

public class TopicCellWithImage extends JPanel implements VerticalList.ListItemListener, Observer {

	private static final Font BorderTitleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);

	private static final Color SelectedColor = new Color(0xff, 0xff, 0xcc);

	private TitledBorder border;

	private JTextArea text;
	private JLabel image;

	private Topic topic;

	private boolean isNewWindowEnabled;

	public TopicCellWithImage(Topic topic, boolean isNewWindowEnabled) {
		this.topic = topic;
		this.isNewWindowEnabled = isNewWindowEnabled;
		init();
	}

	private void init() {
		text = new JTextArea();
		text.setEditable(false);
		text.setLineWrap(true);
		text.setFocusable(false);
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				text.getParent().dispatchEvent(e);
			}
		});
		text.setOpaque(false);
		text.setText(topic.toFormattedString());

		image = new JLabel();
		if (!topic.isLoaded()) {
			image.setText("图片加载中...");
			topic.addObserver(this);
			topic.loadImage();
		} else {
			setImage();
		}
		image.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		image.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (topic.imageIcon != null)
						new ImageWindow(topic.imageIcon).init();
				}
			}
		});

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gb);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridx = 0;
		gb.setConstraints(image, gbc);
		add(image);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gb.setConstraints(text, gbc);
		add(text);

		border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.ORANGE, 5), null,
				TitledBorder.LEFT, TitledBorder.TOP, BorderTitleFont);
		border.setTitle("#" + topic.pid);
		setBorder(border);

		setBackground(Color.WHITE);
	}

	public void refresh() {
		text.setText(topic.toFormattedString());
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

	private void setImage() {
		if (topic.scaledImageIcon == null) {
			if (topic.isLoaded())
				image.setText("图片加载失败！原因：图片损坏或已被删除。");
			else
				image.setText("图片加载失败！原因：网络不畅。建议检查网络连接后刷新重试。");
		} else {
			image.setText(null);
			int width = topic.scaledImageIcon.getIconWidth();
			int height = topic.scaledImageIcon.getIconHeight();
			image.setIcon(topic.scaledImageIcon);
			image.setSize(width, height);
			image.setToolTipText("双击查看大图");
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		setImage();
		o.deleteObserver(this);
	}

}
