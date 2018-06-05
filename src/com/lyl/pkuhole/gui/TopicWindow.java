package com.lyl.pkuhole.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.http.util.TextUtils;

import com.lyl.pkuhole.PKUHole;
import com.lyl.pkuhole.model.AttentionManager;
import com.lyl.pkuhole.model.Comment;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.network.Network;
import com.lyl.pkuhole.utils.UIUtils;
import com.lyl.pkuhole.widgets.CommentCell;
import com.lyl.pkuhole.widgets.TopicCell;
import com.lyl.pkuhole.widgets.TopicCellWithImage;
import com.lyl.pkuhole.widgets.JVerticalList;

import io.reactivex.schedulers.Schedulers;

public class TopicWindow extends JFrame implements Observer {

	private Topic topic;

	// Components for header
	private JButton refresh, report, comment;
	private JCheckBox attention;
	// Panel for header
	private JPanel header;
	// TopicCell
	private JComponent topicCell;
	// VerticalList for comments
	private JVerticalList commentList;
	private JScrollPane scrollPane;

	public TopicWindow(Topic topic) {
		this.topic = topic;
	}

	public void init() {
		PKUHole.getInstance().addObserver(this);
		initComponent();
		initLayout();
		initEvent();
		loadPage();
		invalidateUser();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				PKUHole.getInstance().deleteObserver(TopicWindow.this);
			}
		});
		setMinimumSize(new Dimension(1000, 600));
		setPreferredSize(new Dimension(1000, 800));
		setTitle("����#" + topic.pid);
		pack();
		setVisible(true);
	}

	private void initComponent() {
		refresh = new JButton("ˢ��");
		report = new JButton("�ٱ�");
		comment = new JButton("����");
		attention = new JCheckBox("��ע");
		if (PKUHole.getInstance().user != null)
			attention.setSelected(AttentionManager.isAttention(topic.pid));

		topicCell = topic.getCell(false);
		commentList = new JVerticalList();
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
		// Blank
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		JLabel blank = new JLabel();
		gb.setConstraints(blank, gbc);
		header.add(blank);
		gbc.weightx = 0;
		// JButton report
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 0, 0, 40);
		gb.setConstraints(report, gbc);
		header.add(report);
		// JCheckBox attention
		gb.setConstraints(attention, gbc);
		header.add(attention);
		// JButton comment
		gb.setConstraints(comment, gbc);
		header.add(comment);
		// Layout of header finished.
		scrollPane = new JScrollPane(commentList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		setLayout(new BorderLayout());
		add(header, BorderLayout.SOUTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void initEvent() {
		refresh.addActionListener(e -> {
			refreshTopic();
			loadPage();
		});
		report.addActionListener(e -> {
			report();
		});
		attention.addActionListener(e -> {
			setAttention();
		});
		comment.addActionListener(e -> {
			comment(null);
		});
		commentList.setActionListener(e -> {
			JComponent c = commentList.getSelectedJComponent();
			if (c instanceof CommentCell)
				comment(((CommentCell) c).getComment().name);
			else
				comment(null);
		});
	}

	private void refreshTopic() {
		Network.getSingleTopic(topic.pid)
				.observeOn(Schedulers.io())
				.subscribe(newTopic -> {
					/**
					 * Possibilities are that this topic is deleted and getSingleTopic returns null.
					 * In this case, use the previous version of information and warn the user.
					 */
					if (newTopic != null) {
						topic.likenum = newTopic.likenum;
						topic.reply = newTopic.reply;
						if (topicCell instanceof TopicCell) {
							((TopicCell) topicCell).refresh();
						} else {
							((TopicCellWithImage) topicCell).refresh();
						}
						attention.setSelected(AttentionManager.isAttention(topic.pid));
					} else {
						setTitle("����#" + topic.pid + "[����:���������ѱ�ɾ����]");
					}
					if (topicCell instanceof TopicCell) {
						((TopicCell) topicCell).refresh();
					} else {
						((TopicCellWithImage) topicCell).refresh();
					}
				}, err -> {
					UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());// TODO
				});
	}

	private void loadPage() {
		Network.getComments(topic.pid)
				.observeOn(Schedulers.io())
				.subscribe(comments -> {
					commentList.removeAll();
					commentList.addItem(topicCell);
					for (Comment comment : comments) {
						commentList.addItem(new CommentCell(comment));
					}
					commentList.commit();
				}, err -> {
					UIUtils.messageBox("��������ԭ��" + err.getMessage());// TODO
				});
	}

	private void report() {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			// If invalidateUser() works properly, control should never reach here.
			UIUtils.messageBox("����ʧ�ܣ����ȵ�¼��");
			return;
		}
		String reason = UIUtils.inputBox("������ٱ����ɣ�", "", "�ٱ�");
		if (reason != null)
			reason = reason.trim();
		if (TextUtils.isEmpty(reason)) {
			UIUtils.messageBox("�ٱ�ʧ�ܣ����ɲ���Ϊ�գ�");
		} else {
			Network.report(user.token, topic.pid, reason)
					.observeOn(Schedulers.io())
					.subscribe(obj -> {
					}, err -> {
						UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());
					});
		}
	}

	private void setAttention() {
		boolean selected = attention.isSelected();
		User user = PKUHole.getInstance().user;
		if (user == null) {
			// If invalidateUser() works properly, control should never reach here.
			UIUtils.messageBox("����ʧ�ܣ����ȵ�¼��");
			return;
		}
		Network.setAttention(user.token, topic.pid, selected)
				.observeOn(Schedulers.io())
				.subscribe(obj -> {
					if (selected)
						AttentionManager.addAttentionTopic(topic);
					else
						AttentionManager.removeAttentionTopic(topic.pid);
				}, err -> {
					UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());// TODO
					if (err.getMessage().startsWith("�Ѿ���ע")) {
						attention.setSelected(true);
						AttentionManager.addAttentionTopic(topic);
					}
				});
	}

	public void comment(String name) {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			UIUtils.messageBox("����ʧ�ܣ����ȵ�¼��");
			return;
		}
		String content = UIUtils.inputBox("�������������ݣ�", name == null || name == "¥��" ? "" : "Re " + name + ":", "��������");
		if (content == null)
			return;
		content = content.trim();
		if (TextUtils.isEmpty(content)) {
			UIUtils.messageBox("���������ݣ�");
			return;
		}
		Network.sendComment(user.token, topic.pid, content)
				.observeOn(Schedulers.io())
				.subscribe(pid -> {
					refreshTopic();
					loadPage();
					attention.setSelected(true);
					AttentionManager.addAttentionTopic(topic);
				}, err -> {
					UIUtils.messageBox("����ʧ�ܣ�ԭ��" + err.getMessage());// TODO
				});

	}

	public void invalidateUser() {
		if (PKUHole.getInstance().user == null) {
			report.setEnabled(false);
			attention.setSelected(false);
			attention.setEnabled(false);
			comment.setEnabled(false);
		} else {
			report.setEnabled(true);
			attention.setSelected(AttentionManager.isAttention(topic.pid));
			attention.setEnabled(true);
			comment.setEnabled(true);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		invalidateUser();
	}

}
