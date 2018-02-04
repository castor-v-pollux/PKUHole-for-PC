package com.lyl.pkuhole.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ImageWindow extends JFrame {

	private ImageIcon icon;

	private JLabel label;
	private JScrollPane scrollPane;

	public ImageWindow(ImageIcon icon) {
		this.icon = icon;
	}

	public void init() {
		Dimension size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		label = new JLabel();
		label.setIcon(icon);
		label.setPreferredSize(size);
		scrollPane = new JScrollPane(label);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		setTitle("�鿴ͼƬ");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

}
