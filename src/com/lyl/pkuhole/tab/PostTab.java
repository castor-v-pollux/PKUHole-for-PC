package com.lyl.pkuhole.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.http.util.TextUtils;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.exception.PKUHoleException;
import com.lyl.pkuhole.model.AttentionManager;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.network.Network;
import com.lyl.pkuhole.utils.BASE64Utils;
import com.lyl.pkuhole.utils.UIUtils;
import com.lyl.pkuhole.widgets.JScrollablePanel;

import io.reactivex.schedulers.Schedulers;

public class PostTab extends JPanel {

	private static final Font BorderTitleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);

	private static final Font LargeFont = new Font(Font.SANS_SERIF, Font.PLAIN, 22);

	private static final String HintNoPicture = "��ǰ��ͼƬ��", HintPreviewPicture = "ͼƬԤ����", HintSelectedPicture = "��ѡ��ͼƬ��";

	private JTextArea text;
	private JButton post, addPic, removePic;
	private JLabel imageHint, imageLabel;
	private JPanel buttonPanel;

	private JScrollablePanel content;

	private BufferedImage image;
	private BufferedImage imagePreview;

	public PostTab() {
		initComponent();
		initLayout();
		initEvent();
	}

	private void initComponent() {
		text = new JTextArea();
		text.setRows(4);
		text.setLineWrap(true);
		text.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.ORANGE, 5), "#??????",
				TitledBorder.LEFT, TitledBorder.TOP, BorderTitleFont));
		text.setFont(LargeFont);
		post = new JButton("����");
		post.setFont(LargeFont);
		addPic = new JButton("���ͼƬ");
		addPic.setFont(LargeFont);
		removePic = new JButton("ɾ��ͼƬ");
		removePic.setFont(LargeFont);
		removePic.setVisible(false);
		imageHint = new JLabel(HintNoPicture);
		imageHint.setFont(LargeFont);
		imageLabel = new JLabel();

		// JPanel buttonPanel
		buttonPanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		buttonPanel.setLayout(gb);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.weighty = 1;
		gb.setConstraints(post, gbc);
		buttonPanel.add(post);
		gb.setConstraints(addPic, gbc);
		buttonPanel.add(addPic);
		gb.setConstraints(removePic, gbc);
		buttonPanel.add(removePic);

		content = new JScrollablePanel();
	}

	private void initLayout() {
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		content.setLayout(gb);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(40, 40, 0, 40);
		// JTextArea text
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gb.setConstraints(text, gbc);
		content.add(text);
		// JPanel buttonPanel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gb.setConstraints(buttonPanel, gbc);
		content.add(buttonPanel);
		// JLabel imageHint
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gb.setConstraints(imageHint, gbc);
		content.add(imageHint);
		// JLabel imageLabel
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 3;
		gbc.weighty = 1;
		gb.setConstraints(imageLabel, gbc);
		content.add(imageLabel);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void initEvent() {
		post.addActionListener(e -> {
			User user = PKUHole.getInstance().user;
			if (user == null) {
				// If invalidateUser() works properly, control should never reach here.
				UIUtils.messageBox("����ʧ�ܣ����ȵ�¼��");
				return;
			}
			String content = text.getText().trim();
			if (TextUtils.isEmpty(content) && image == null) {
				UIUtils.messageBox("���������ݻ�ѡ��ͼƬ��");
				return;
			}
			if (image == null)
				// ��������
				Network.sendTextPost(user.token, content)
						.observeOn(Schedulers.io())
						.subscribe(pid -> {
							reset();
							Network.getSingleTopic(pid)
									.observeOn(Schedulers.io())
									.subscribe(topic -> {
										AttentionManager.addAttentionTopic(topic);
										UIUtils.messageBox("����ɹ���������Ϊ" + pid);
									}, err -> {
									});
						}, err -> {
							UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());
						});
			else
				try {
					Network.sendImagePost(user.token, content, BASE64Utils.imageToString(image))
							.observeOn(Schedulers.io())
							.subscribe(pid -> {
								// ����ͼƬ����������������Զ���ע(����������)
								UIUtils.messageBox("����ɹ������ڷ��������⣬����ͼƬ���������Զ���ע�������������ҳ�ֶ���ע");
								reset();
							}, err -> {
								UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());
							});
				} catch (PKUHoleException err) {
					UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());
				}

		});
		addPic.addActionListener(e -> {
			chooseImage();
		});
		removePic.addActionListener(e -> {
			image = null;
			imageLabel.setIcon(null);
			imageHint.setText(HintNoPicture);
			addPic.setText("���ͼƬ");
			removePic.setVisible(false);
		});
	}

	private void reset() {
		text.setText(null);
		addPic.setText("���ͼƬ");
		removePic.setVisible(false);
		imageHint.setText(HintNoPicture);
		imageLabel.setIcon(null);
		image = null;
	}

	private void chooseImage() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setFileFilter(new ImageFileFilter());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addPropertyChangeListener(e -> {
			if (e.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
				File f = (File) e.getNewValue();
				if (f == null || f.isDirectory())
					return;
				imagePreview = loadImageFromFile(f);
				if (imagePreview == null)
					return;
				setImage(imagePreview);
				imageHint.setText(HintPreviewPicture);
			}
		});
		int result = chooser.showDialog(null, "ѡ��ͼƬ");
		if (result == JFileChooser.APPROVE_OPTION) {
			if (imagePreview == null) {
				UIUtils.messageBox("ͼƬ����ʧ�ܣ�ԭ������У�ͼƬ��ʽ����ͼƬ�ļ��𻵣�ͼƬ����ȡ�������ѡ��");
				restorePreviousImage();
			} else {
				imageHint.setText(HintSelectedPicture);
				image = imagePreview;
				addPic.setText("����ͼƬ");
				removePic.setVisible(true);
				imagePreview = null;
			}
		} else {
			restorePreviousImage();
		}
	}

	private void restorePreviousImage() {
		if (image == null) {
			imageHint.setText(HintNoPicture);
			imageLabel.setIcon(null);
		} else {
			imageHint.setText(HintSelectedPicture);
			setImage(image);
		}
	}

	private void setImage(BufferedImage image) {
		ImageIcon icon = new ImageIcon(image);
		if (icon.getIconHeight() > UIUtils.MAX_IMAGE_HEIGHT)
			icon = new ImageIcon(imagePreview.getScaledInstance(-1, UIUtils.MAX_IMAGE_HEIGHT, Image.SCALE_DEFAULT));
		imageLabel.setIcon(icon);
	}

	private BufferedImage loadImageFromFile(File file) {
		BufferedImage image;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		}
		return image;
	}

	private static class ImageFileFilter extends FileFilter {

		private static final String[] imageSuffixes = ImageIO.getReaderFileSuffixes();

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			String name = f.getName().toLowerCase();
			for (String suffix : imageSuffixes) {
				if (name.endsWith(suffix))
					return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			StringBuilder sb = new StringBuilder();
			for (String suffix : imageSuffixes)
				sb.append(suffix).append(' ');
			return sb.toString();
		}

	}
}
