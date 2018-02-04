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
import com.lyl.pkuhole.PKUHoleAPI;
import com.lyl.pkuhole.exception.PKUHoleException;
import com.lyl.pkuhole.model.AttentionManager;
import com.lyl.pkuhole.model.Comment;
import com.lyl.pkuhole.model.Topic;
import com.lyl.pkuhole.model.User;
import com.lyl.pkuhole.utils.UIUtils;
import com.lyl.pkuhole.widgets.CommentCell;
import com.lyl.pkuhole.widgets.TopicCell;
import com.lyl.pkuhole.widgets.TopicCellWithImage;
import com.lyl.pkuhole.widgets.VerticalList;

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
	private VerticalList commentList;
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
		setTitle("树洞#" + topic.pid);
		pack();
		setVisible(true);
	}

	private void initComponent() {
		refresh = new JButton("刷新");
		report = new JButton("举报");
		comment = new JButton("评论");
		attention = new JCheckBox("关注");
		if (PKUHole.getInstance().user != null)
			attention.setSelected(AttentionManager.isAttention(topic.pid));

		topicCell = topic.getCell(false);
		commentList = new VerticalList();
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
		try {
			Topic newTopic = PKUHoleAPI.getSingleTopic(topic.pid);
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
				setTitle("树洞#" + topic.pid + "[警告:本条树洞已被删除！]");
			}
			if (topicCell instanceof TopicCell) {
				((TopicCell) topicCell).refresh();
			} else {
				((TopicCellWithImage) topicCell).refresh();
			}
		} catch (PKUHoleException e) {
			UIUtils.messageBox("操作失败，原因：" + e.getMessage());
		}
	}

	private void loadPage() {
		try {
			Comment[] comments = PKUHoleAPI.getComments(topic.pid);
			commentList.removeAll();
			commentList.addItem(topicCell);
			for (Comment comment : comments) {
				commentList.addItem(new CommentCell(comment));
			}
			commentList.commit();
		} catch (PKUHoleException e) {
			UIUtils.messageBox("发生错误，原因：" + e.getMessage());
		}
	}

	private void report() {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			// If invalidateUser() works properly, control should never reach here.
			UIUtils.messageBox("操作失败，请先登录！");
			return;
		}
		String reason = UIUtils.inputBox("请输入举报理由：", "", "举报");
		if (reason != null)
			reason = reason.trim();
		if (TextUtils.isEmpty(reason)) {
			UIUtils.messageBox("举报失败：理由不能为空！");
		} else {
			try {
				PKUHoleAPI.report(user.token, topic.pid, reason);
			} catch (PKUHoleException e) {
				UIUtils.messageBox("操作失败！原因：" + e.getMessage());
			}
		}
	}

	/**
	 * boolean selected = attention.isSelected(); if (!setAttention(selected)) {
	 * attention.setSelected(!selected); }
	 * 
	 * @param attention
	 * @return
	 */

	private void setAttention() {
		boolean selected = attention.isSelected();
		User user = PKUHole.getInstance().user;
		if (user == null) {
			// If invalidateUser() works properly, control should never reach here.
			UIUtils.messageBox("操作失败，请先登录！");
			return;
		}
		try {
			PKUHoleAPI.setAttention(user.token, topic.pid, selected);
			if (selected)
				AttentionManager.addAttentionTopic(topic);
			else
				AttentionManager.removeAttentionTopic(topic.pid);
		} catch (PKUHoleException e) {
			UIUtils.messageBox("操作失败，原因：" + e.getMessage());
			if (e.getMessage().startsWith("已经关注")) {
				attention.setSelected(true);
				AttentionManager.addAttentionTopic(topic);
			}
		}
	}

	public void comment(String name) {
		User user = PKUHole.getInstance().user;
		if (user == null) {
			UIUtils.messageBox("评论失败，请先登录！");
			return;
		}
		String content = UIUtils.inputBox("请输入评论内容：", name == null || name == "楼主" ? "" : "Re " + name + ":", "评论树洞");
		if (content == null)
			return;
		content = content.trim();
		if (TextUtils.isEmpty(content)) {
			UIUtils.messageBox("请输入内容！");
			return;
		}
		try {
			PKUHoleAPI.sendComment(user.token, topic.pid, content);
			refreshTopic();
			loadPage();
			attention.setSelected(true);
			AttentionManager.addAttentionTopic(topic);
		} catch (PKUHoleException e) {
			UIUtils.messageBox("评论失败！原因：" + e.getMessage());
		}

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
